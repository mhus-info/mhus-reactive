package de.mhus.cherry.reactive.util.designer;

import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.mhus.cherry.reactive.model.annotations.Trigger;

public abstract class XBEvent {

	private String outgoing;
	private String id;
	private String name;

	public void update(Element eType, String outgoingRef, Element elem) {
		this.outgoing = outgoingRef;
		this.id = elem.getAttribute("id");
		this.name = elem.getAttribute("name");
	}

	public String getOutgoing() {
		return outgoing;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void createXml(Element xml, XElement elem, TreeMap<String, XElement> elements, int cnt) {
//	    <bpmn2:boundaryEvent id="BoundaryEvent_2" name="" attachedToRef="Task_3">
//	      <bpmn2:outgoing>SequenceFlow_19</bpmn2:outgoing>
//	      <bpmn2:timerEventDefinition id="TimerEventDefinition_1"/>
//	    </bpmn2:boundaryEvent>
		Document doc = xml.getOwnerDocument();
		Element eEvent = doc.createElement("bpmn2:boundaryEvent");
		id = elem.getId() + "-" + cnt;
		eEvent.setAttribute("id", id);
		eEvent.setAttribute("name", name);
		eEvent.setAttribute("attachedToRef", elem.getId());
		xml.appendChild(eEvent);
		
		if (outgoing != null) {
			Element eOut = doc.createElement("bpmn2:outgoing");
			Text text = doc.createTextNode(XElement.SEQUENCE_FLOW + getId() + "_" + outgoing);
			eOut.appendChild(text);
			eEvent.appendChild(eOut);
		}
		
		String xmlName = getXmlElementName();
		if (xmlName != null) {
			Element eType = doc.createElement(xmlName);
			eType.setAttribute("id", id + "-Definition");
			eEvent.appendChild(eType);
		}
		
	}

	protected abstract String getXmlElementName();

	public void update(XElement xElement, Trigger trigger, int cnt) {
		id = xElement.getId() + "-" + cnt;
		name = trigger.name();
		outgoing = trigger.activity().getCanonicalName();
	}

}
