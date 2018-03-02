package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.engine.ProcessContext;

public interface Activity<P extends Pool> {

	ProcessContext<P> getContext();
	
	default P getPool() {
		return getContext().getPool();
	}
	
}
