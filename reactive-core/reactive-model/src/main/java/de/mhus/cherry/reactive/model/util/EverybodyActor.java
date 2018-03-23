package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AActor;

public class EverybodyActor implements AActor {

	@Override
	public boolean hasAccess(String user) {
		return true;
	}

}
