package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

public class PCaseInfo {

	private UUID id;
	private String uri;

	public PCaseInfo(UUID id, String uri) {
		this.id = id;
		this.uri = uri;
	}

	public UUID getId() {
		return id;
	}
	
	public String getUri() {
		return uri;
	}

}
