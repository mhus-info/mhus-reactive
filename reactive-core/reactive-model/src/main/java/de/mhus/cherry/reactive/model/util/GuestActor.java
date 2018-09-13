package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AActor;

public class GuestActor implements AActor {

	public static final String USERNAME = "guest";
	
	@Override
	public boolean hasAccess(String user) {
		return USERNAME.equals(user);
	}

}
