package de.mhus.cherry.reactive.model.ui;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;

public interface ICase {

	String getUri();

	String getCaseCanonicalName();

	String getIndexValue(int index);

	String getIndexDisplayName(int index);
	
	String getCustomId();

	String getCustomerId();
	
	STATE_CASE getState();

	UUID getId();

	String getDisplayName();

	String getDescription();


}
