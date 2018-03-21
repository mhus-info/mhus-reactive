package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

public class PCaseInfo {

	private UUID id;
	private String uri;
	private String canonicalName;

	public PCaseInfo(UUID id, String uri, String canonicalName) {
		this.id = id;
		this.uri = uri;
		this.canonicalName = canonicalName;
	}

	public UUID getId() {
		return id;
	}
	
	public String getUri() {
		return uri;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

}
