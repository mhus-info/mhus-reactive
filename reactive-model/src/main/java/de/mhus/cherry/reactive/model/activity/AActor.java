package de.mhus.cherry.reactive.model.activity;

public interface AActor<P extends APool<?>> extends AElement<P> {

	boolean hasAccess(String user);
}
