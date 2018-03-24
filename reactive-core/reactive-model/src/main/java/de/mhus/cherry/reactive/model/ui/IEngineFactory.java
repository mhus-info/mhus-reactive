package de.mhus.cherry.reactive.model.ui;

import java.util.Locale;

public interface IEngineFactory {

	IEngine create(String user, Locale locale);
	
}
