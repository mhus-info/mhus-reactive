package de.mhus.cherry.reactive.model.ui;

import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;

public interface ICase {

	String getUri();

	String getCanonicalName();

	String getCustomId();

	String getCustomerId();
	
	STATE_CASE getState();

	UUID getId();

	Map<String,String> getProperties();

}
