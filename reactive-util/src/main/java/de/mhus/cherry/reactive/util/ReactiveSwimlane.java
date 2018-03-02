package de.mhus.cherry.reactive.util;

import de.mhus.cherry.reactive.model.activity.Actor;
import de.mhus.cherry.reactive.model.activity.Swimlane;
import de.mhus.cherry.reactive.model.activity.Pool;

public class ReactiveSwimlane<P extends Pool> implements Swimlane<P> {

	@Override
	public Class<? extends Actor<P>> getActor() {
		// TODO Auto-generated method stub
		return null;
	}

}
