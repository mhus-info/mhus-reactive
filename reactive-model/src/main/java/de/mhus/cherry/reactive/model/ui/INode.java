package de.mhus.cherry.reactive.model.ui;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class INode {

	private String uri;
	private String caseName;
	private String caseCanonicalName;
	private ActivityDescription caseDescription;
	private String nodeName;
	private String nodeCanonicalName;
	private STATE_NODE nodeSate;
	private ActivityDescription nodeDescription;

	public INode(ProcessContext<?> context, PCase caze, PNode node) {
		this.uri = caze.getUri();
		this.caseName = caze.getName();
		this.caseCanonicalName = caze.getCanonicalName();
		this.caseDescription = context.getEPool().getElement(caze.getCanonicalName()).getActivityDescription();
		
		this.nodeName = node.getName();
		this.nodeCanonicalName = node.getCanonicalName();
		this.nodeSate = node.getState();
		this.nodeDescription = context.getEPool().getElement(node.getCanonicalName()).getActivityDescription();
		
	}

	public String getUri() {
		return uri;
	}

	public String getCaseName() {
		return caseName;
	}

	public String getCaseCanonicalName() {
		return caseCanonicalName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getNodeCanonicalName() {
		return nodeCanonicalName;
	}

	public STATE_NODE getNodeSate() {
		return nodeSate;
	}

	public String getNodeDisplayName() {
		return nodeDescription.displayName();
	}
	
	public String getNodeDescription() {
		return nodeDescription.description();
	}

	public String getCaseDisplayName() {
		return caseDescription.displayName();
	}
	
	public String getCaseDescription() {
		return caseDescription.description();
	}

}
