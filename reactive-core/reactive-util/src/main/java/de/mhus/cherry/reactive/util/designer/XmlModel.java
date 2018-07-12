package de.mhus.cherry.reactive.util.designer;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AEndPoint;
import de.mhus.cherry.reactive.model.activity.AExclusiveGateway;
import de.mhus.cherry.reactive.model.activity.AParallelGateway;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.AStartPoint;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.activity.ATask;
import de.mhus.cherry.reactive.model.activity.AUserTask;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MXml;
import de.mhus.lib.errors.UsageException;

public class XmlModel extends MLog {
	
	private TreeMap<String, XElement> elements = new TreeMap<>();
	private String poolId;
	private String poolName;
	
	public void load(Element xml) {
		if (!xml.getNodeName().equals("bpmn2:process"))
			throw new UsageException("not a bpmn2:process node");
		
		poolId = xml.getAttribute("id");
		poolName = xml.getAttribute("name");
		
		elements.clear();
		
		HashMap<String,String> laneRefs = new HashMap<>();
		
		for (Element elem : MXml.getLocalElementIterator(xml)) {
			String eName = elem.getNodeName();
			if (eName.equals("bpmn2:laneSet")) {
				// load lanes
				for (Element eLane : MXml.getLocalElementIterator(elem)) {
					if (eLane.getNodeName().equals("bpmn2:lane")) {
						XElement entry = new XLane();
						entry.doUpdate(eLane);
						elements.put(eLane.getAttribute("id"), entry);
						for (Element eRef : MXml.getLocalElementIterator(eLane, "bpmn2:flowNodeRef")) {
							laneRefs.put(MXml.getValue(eRef, false), entry.getId());
						}
					}
				}
			} else
			if (eName.equals("bpmn2:userTask")) {
				XElement entry = new XUserTask();
				entry.doUpdate(elem);
				elements.put(elem.getAttribute("id"), entry);
			} else
			if (eName.equals("bpmn2:scriptTask")) {
				XElement entry = new XScriptTask();
				entry.doUpdate(elem);
				elements.put(elem.getAttribute("id"), entry);
			} else
			if (eName.equals("bpmn2:exclusiveGateway")) {
				XElement entry = new XExclusiveGateway();
				entry.doUpdate(elem);
				elements.put(elem.getAttribute("id"), entry);
			} else
			if (eName.equals("bpmn2:parallelGateway")) {
				XElement entry = new XParallelGateway();
				entry.doUpdate(elem);
				elements.put(elem.getAttribute("id"), entry);
			} else
			if (eName.equals("bpmn2:startEvent")) {
				XElement entry = new XStartEvent();
				entry.doUpdate(elem);
				elements.put(elem.getAttribute("id"), entry);
			} else
			if (eName.equals("bpmn2:endEvent")) {
				XElement entry = new XEndEvent();
				entry.doUpdate(elem);
				elements.put(elem.getAttribute("id"), entry);
			} else
			if (eName.equals("bpmn2:sequenceFlow")) {
				// ignore
				// <bpmn2:sequenceFlow id="SequenceFlow_8" sourceRef="ScriptTask_1" targetRef="ParallelGateway_1"/>
			} else
			if (eName.equals("bpmn2:boundaryEvent")) {
				// ignore
				// <bpmn2:boundaryEvent id="BoundaryEvent_1" name="" attachedToRef="Task_2">
			} else {
				log().i("Unknown element", eName);
				XElement entry = new XUnknown();
				entry.doUpdate(elem);
				elements.put(elem.getAttribute("id"), entry);
			}
		}
		
		// boundary events
		for (Element elem : MXml.getLocalElementIterator(xml)) {
			String eName = elem.getNodeName();
			if (eName.equals("bpmn2:boundaryEvent")) {
				// <bpmn2:boundaryEvent id="BoundaryEvent_1" name="" attachedToRef="Task_2">
				String ref = elem.getAttribute("attachedToRef");
				XElement master = elements.get(ref);
				if (master == null) {
					log().w("master not found for boundary",eName, ref);
				} else {
					XBEvent boundary = createBoundary(elem);
					master.addBoundary(boundary);
				}
			}
		}
		
		// connect Lane
		elements.values().forEach(v -> v.connectLane(laneRefs));
	}
	
	private XBEvent createBoundary(Element elem) {
		// <bpmn2:boundaryEvent id="BoundaryEvent_2" name="" attachedToRef="Task_3">
		Element eOutgoing = null;
		Element eType = null;
		for (Element child : MXml.getLocalElementIterator(elem)) {
			String eName = child.getNodeName();
			if (eName.equals("bpmn2:outgoing")) {
				eOutgoing = child;
			} else {
				eType = child;
			}
		}
		
		String outgoingRef  = null;
		if (eOutgoing != null)
			outgoingRef = MXml.getValue(eOutgoing, false);
		
		XBEvent xb = null;
		if (eType == null) 
			xb = new XBUnknown();
		else {
			String type = eType.getNodeName();
			if (type.equals("bpmn2:signalEventDefinition"))
				xb = new XBSignalEvent();
			else
			if (type.equals("bpmn2:errorEventDefinition"))
				xb = new XBErrorEvent();
			else
			if (type.equals("bpmn2:timerEventDefinition"))
				xb = new XBTimerEvent();
			else
				xb = new XBUnknown();
		}
		xb.update(eType, outgoingRef, elem);
		return xb;
	}

	public void merge(EPool pool) {
		
		poolId = pool.getCanonicalName();
		poolName = pool.getName();
		
		// set unused
		elements.values().forEach(v -> v.setUsed(false));
		
		boolean needDefaultLane = false;
		// parse elements
		for (String name : pool.getElementNames()) {
			EElement element = pool.getElement(name);

			String cName = element.getCanonicalName();
			XElement entry = elements.get(cName);
			Class<? extends XElement>clazz = findClass(element);
			if (clazz != null) {
				if (entry == null || !entry.getClass().getCanonicalName().equals(clazz.getCanonicalName())) {
					try {
						entry = clazz.newInstance();
						elements.put(cName, entry);
						entry.doUpdate(element);
						entry.setUsed(true);
					} catch (InstantiationException | IllegalAccessException e) {
						log().e(clazz, e);
					}
				} else {
					entry.doUpdate(element);
					entry.setUsed(true);
				}
				if (entry != null && XElement.DEFAULT_LANE.equals(entry.getLaneId()))
					needDefaultLane = true;
			}
		}
		
		// check for default lane
		if (needDefaultLane) {
			XElement entry = elements.get(XElement.DEFAULT_LANE);
			if (entry == null) {
				entry = new XDefaultLane();
				elements.put(entry.getId(), entry);
				entry.setUsed(true);
			} else {
				entry.setUsed(true);
			}
		}
		
		// remove unused
		elements.values().removeIf(v -> !v.isUsed());
		
		// connect incoming
		elements.values().forEach(v -> v.connectIncoming(elements));
				
	}

	private Class<? extends XElement> findClass(EElement element) {
		Class<? extends AElement<?>> clazz = element.getElementClass();
		if (ASwimlane.class.isAssignableFrom(clazz))
			return XLane.class;
		if (AEndPoint.class.isAssignableFrom(clazz))
			return XEndEvent.class;
		if (AStartPoint.class.isAssignableFrom(clazz))
			return XStartEvent.class;
		if (AUserTask.class.isAssignableFrom(clazz))
			return XUserTask.class;
		if (ATask.class.isAssignableFrom(clazz))
			return XScriptTask.class;
		if (AExclusiveGateway.class.isAssignableFrom(clazz))
			return XExclusiveGateway.class;
		if (AParallelGateway.class.isAssignableFrom(clazz))
			return XParallelGateway.class;
		if (APool.class.isAssignableFrom(clazz))
			return null; // ignore pool
		log().w("Unknown",element.getCanonicalName(),element.getElementClass());
		return XUnknown.class;
	}

	public void createXml(Element xml) {
		if (!xml.getNodeName().equals("bpmn2:process"))
			throw new UsageException("not a bpmn2:process node");

		Document doc = xml.getOwnerDocument();
		
		xml.setAttribute("id", poolId);
		xml.setAttribute("name", poolName);
		xml.setAttribute("isExecutable", "false");
		
		// first node is the lane set
		Element laneSet = doc.createElement("bpmn2:laneSet");
		laneSet.setAttribute("id", "LaneSet_1");
		laneSet.setAttribute("name", "Lane Set 1");
		xml.appendChild(laneSet);
		
		// create elements
		TreeSet<String> flows = new TreeSet<>();
		for (XElement elem : elements.values()) {
			if (elem instanceof XLane) {
				elem.createXml(laneSet, elements);
			} else {
				elem.createXml(xml, elements);
				for (String ref : elem.getIncoming())
					flows.add(ref + "_" + elem.getId());
				for (String ref : elem.getOutgoing())
					flows.add(elem.getId() + "_" + ref);
				
				int cnt = 0;
				for (XBEvent event : elem.getBoundaries()) {
					event.createXml(xml, elem, elements, cnt);
					cnt++;
					if (event.getOutgoing() != null) {
						flows.add(event.getId() + "_" + event.getOutgoing());
					}
				}
			}
		}
		
		// create  "SequenceFlow_" notes
		for (String flow : flows) {
			String id = XElement.SEQUENCE_FLOW + flow;
			int p = flow.indexOf('_');
			String from = flow.substring(0, p);
			String to = flow.substring(p+1);
			//<bpmn2:sequenceFlow id="SequenceFlow_1" sourceRef="Task_1" targetRef="UserTask_1"/>
			Element cFlow = doc.createElement("bpmn2:sequenceFlow");
			cFlow.setAttribute("id", id);
			cFlow.setAttribute("sourceRef", from);
			cFlow.setAttribute("targetRef", to);
			xml.appendChild(cFlow);
		}
		
	}

	public void dump() {
		for (Entry<String, XElement> entry : elements.entrySet()) {
			System.out.println(">>> " + entry.getKey());
			XElement v = entry.getValue();
			System.out.println("  ID  : " + v.getId());
			System.out.println("  Type: " + v.getClass().getCanonicalName());
			System.out.println("  Name: " + v.getName());
			System.out.println("  Lane: " + v.getLaneId());
			for (String ref : v.getIncoming())
				System.out.println("    In : " + ref);
			for (String ref : v.getOutgoing())
				System.out.println("    Out: " + ref);
			for (XBEvent event : v.getBoundaries())
				System.out.println("    Event: " + event.getClass().getSimpleName() + " " + event.getOutgoing());
		}
	}
	
	public String getPoolId() {
		return poolId;
	}
	
	public String getPoolName() {
		return poolName;
	}

	public static XBEvent createBoundary(XElement xElement, Trigger trigger, int cnt) {
		XBEvent out = null;
		switch (trigger.type()) {
		case DEFAULT_ERROR:
			out = new XBErrorEvent();
			break;
		case ERROR:
			out = new XBErrorEvent();
			break;
		case MESSAGE:
			out = new XBMessageEvent();
			break;
		case NOOP:
			out = new XBUnknown();
			break;
		case SIGNAL:
			out = new XBSignalEvent();
			break;
		case TIMER:
			out = new XBTimerEvent();
			break;
		default:
			out = new XBUnknown();
			break;
		}
		out.update(xElement, trigger, cnt);
		return out;
	}
	
}