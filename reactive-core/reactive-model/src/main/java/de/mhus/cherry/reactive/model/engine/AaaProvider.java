package de.mhus.cherry.reactive.model.engine;

public interface AaaProvider {

	String getCurrentUserId();

	boolean hasAdminAccess(String user);

	boolean hasGroupAccess(String user, String group);

	boolean validatePassword(String user, String pass);

	boolean isUserActive(String user);

	boolean hasUserGeneralActorAccess(String uri, String canonicalName, String user);

}
