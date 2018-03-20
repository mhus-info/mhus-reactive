package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

public class PNodeInfo {

	private UUID id;
	private UUID caseId;

	public PNodeInfo(UUID id, UUID caseId) {
		this.id = id;
		this.caseId = caseId;
	}

	public UUID getId() {
		return id;
	}

	public UUID getCaseId() {
		return caseId;
	}
	
}
