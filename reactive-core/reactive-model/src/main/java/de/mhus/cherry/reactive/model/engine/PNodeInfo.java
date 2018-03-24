package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;

public class PNodeInfo {

	private UUID id;
	private UUID caseId;
	private String canonicalName;
	private String assigned;
	private STATE_NODE state;
	private TYPE_NODE type;
	private String customId;
	private String uri;
	private String[] indexValues;

	public PNodeInfo(UUID id, UUID caseId, String canonicalName, String assigned, STATE_NODE state, TYPE_NODE type, String uri, String customId, String[] indexValues) {
		this.id = id;
		this.caseId = caseId;
		this.canonicalName = canonicalName;
		this.assigned = assigned;
		this.state = state;
		this.type = type;
		this.uri = uri;
		this.customId = customId;
		this.indexValues = indexValues;
	}

	public PNodeInfo(PCaseInfo cazeInfo, PNode node) {
		this(node.getId(), node.getCaseId(),node.getCanonicalName(),node.getAssignedUser(),node.getState(),node.getType(), cazeInfo.getCustomId(), cazeInfo.getUri(), null);
	}

	public UUID getId() {
		return id;
	}

	public UUID getCaseId() {
		return caseId;
	}
	
	public String getCanonicalName() {
		return canonicalName;
	}
	
	public String getAssigned() {
		return assigned;
	}

	public STATE_NODE getState() {
		return state;
	}

	public TYPE_NODE getType() {
		return type;
	}

	@Override
	public String toString() {
		return id + " " + caseId + " " + canonicalName + " " + assigned + " " + state + " " + type;
	}
	
	public String getUri() {
		return uri;
	}

	public String getCustomId() {
		return customId;
	}
	
	public String getIndexValue(int index) {
		if (indexValues == null || index < 0 || index >= indexValues.length) return null;
		return indexValues[index];
	}


}
