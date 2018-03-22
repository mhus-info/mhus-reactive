package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AActor;

public class NobodyActor implements AActor {

	@Override
	public boolean hasAccess(String user) {
		return false;
	}

}
