package de.mhus.cherry.reactive.util.designer;

import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XLane extends XElement {

	@Override
	public Element createXml(Element laneSet, TreeMap<String, XElement> elements) {
		
		// paranoia
		outgoing.clear();
		incoming.clear();
		
		// create node
		Element lane = super.createXml(laneSet, elements);
		
		// fill with refs
		Document doc = laneSet.getOwnerDocument();
		for (XElement element : elements.values()) {
			if (!(element instanceof XLane) && id.equals(element.getLaneId())) {
				Element ref = doc.createElement("bpmn2:flowNodeRef");
				lane.appendChild(ref);
				Text text = doc.createTextNode(element.getId());
				ref.appendChild(text);
				
				for (XBEvent event : element.getBoundaries()) {
					ref = doc.createElement("bpmn2:flowNodeRef");
					lane.appendChild(ref);
					text = doc.createTextNode(event.getId());
					ref.appendChild(text);
				}
				
			}
		}
		
		return lane;
	}

	@Override
	protected String getXmlElementName() {
		return "bpmn2:lane";
	}

}
