package de.mhus.cherry.reactive.util.designer;

import java.io.File;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.mhus.lib.core.MXml;

public class DesignerUtil {

	
	public static void createDocument(XmlModel model, File file) throws Exception {
		Document doc = createDocument(model);
		MXml.saveXml(doc.getDocumentElement(), file);
	}
	
	public static Document createDocument(XmlModel model) throws Exception {
		Document doc = MXml.createDocument();
		Element root = doc.createElement("bpmn2:definitions");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xmlns:bpmn2", "http://www.omg.org/spec/BPMN/20100524/MODEL");
		root.setAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
		root.setAttribute("xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC");
		root.setAttribute("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
		root.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
		root.setAttribute("id", UUID.randomUUID().toString().replace('-', 'x'));
		root.setAttribute("exporter", "org.eclipse.bpmn2.modeler.core");
		root.setAttribute("exporterVersion", "1.4.3.Final-v20180418-1358-B1");
		doc.appendChild(root);
		
//		Element cItemDef = doc.createElement("bpmn2:itemDefinition");
//		cItemDef.setAttribute("id", "ItemDefinition_252");
//		cItemDef.setAttribute("isCollection", "false");
//		cItemDef.setAttribute("structureRef", "xs:boolean");
//		root.appendChild(cItemDef);
		
		Element cProcess = doc.createElement("bpmn2:process");
		root.appendChild(cProcess);
		model.createXml(cProcess);
		
		return doc;
	}
	
}
