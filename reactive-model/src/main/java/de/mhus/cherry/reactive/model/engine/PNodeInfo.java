package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

public class PNodeInfo {

	private UUID id;
	private UUID caseId;
	private String canonicalName;
	private String assigned;

	public PNodeInfo(UUID id, UUID caseId, String canonicalName, String assigned) {
		this.id = id;
		this.caseId = caseId;
		this.canonicalName = canonicalName;
		this.assigned = assigned;
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
	
}
