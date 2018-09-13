package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AActor;

public class RootActor implements AActor {

	public static final String USERNAME = "root";
	
	@Override
	public boolean hasAccess(String user) {
		return USERNAME.equals(user);
	}

}
