package de.mhus.cherry.reactive.model.activity;

public interface Swimlane<P extends Pool<?>> extends RElement<P> {
	
	Class<? extends Actor<P>> getActor();
	
}
