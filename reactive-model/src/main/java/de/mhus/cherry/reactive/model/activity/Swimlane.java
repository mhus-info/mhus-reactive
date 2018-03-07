package de.mhus.cherry.reactive.model.activity;

public interface Swimlane<P extends Pool<?>> extends Element {
	
	Class<? extends Actor<P>> getActor();
	
}
