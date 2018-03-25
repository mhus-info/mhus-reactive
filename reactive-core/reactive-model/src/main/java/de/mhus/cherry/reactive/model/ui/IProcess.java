package de.mhus.cherry.reactive.model.ui;

public interface IProcess {

	String getDisplayName(String uri, String canonicalName);

	String getDescription(String uri, String canonicalName);

	String getIndexDisplayName(int index, String uri, String canonicalName);

}
