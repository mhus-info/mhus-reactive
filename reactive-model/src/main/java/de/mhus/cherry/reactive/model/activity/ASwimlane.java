package de.mhus.cherry.reactive.model.activity;

public interface ASwimlane<P extends APool<?>> extends AElement<P> {
	
	Class<? extends AActor<P>> getActor();
	
}
