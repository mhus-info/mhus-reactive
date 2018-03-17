package de.mhus.cherry.reactive.model.engine;

public interface AaaProvider {

	String getCurrentUserId();

	boolean hasAdminAccess(String user);

	boolean hasGroupAccess(String user, String group);

}
