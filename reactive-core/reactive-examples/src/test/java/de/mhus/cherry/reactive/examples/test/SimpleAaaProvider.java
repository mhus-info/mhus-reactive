package de.mhus.cherry.reactive.examples.test;

import de.mhus.cherry.reactive.model.engine.AaaProvider;

public class SimpleAaaProvider implements AaaProvider {

	@Override
	public String getCurrentUserId() {
		return "me";
	}

	@Override
	public boolean hasAdminAccess(String user) {
		return false;
	}

	@Override
	public boolean hasGroupAccess(String user, String group) {
		return true;
	}

	@Override
	public boolean validatePassword(String user, String pass) {
		return true;
	}

	@Override
	public boolean isUserActive(String user) {
		return true;
	}

}
