package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.activity.APool;

public class EverybodyActor implements AActor<APool<?>> {

	@Override
	public boolean hasAccess(String user) {
		return true;
	}

}
