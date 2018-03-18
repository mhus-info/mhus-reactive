package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

public class PNodeInfo {

	private UUID id;

	public PNodeInfo(UUID id, UUID caseId) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

}
