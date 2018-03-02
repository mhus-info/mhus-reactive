package de.mhus.cherry.reactive.model.activity;

public interface Swimlane<P extends Pool> {
	
	Class<? extends Actor<P>> getActor();
	
}
