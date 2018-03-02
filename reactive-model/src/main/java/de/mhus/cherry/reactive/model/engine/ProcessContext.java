package de.mhus.cherry.reactive.model.engine;

import de.mhus.cherry.reactive.model.activity.Swimlane;
import de.mhus.cherry.reactive.model.activity.Pool;

public interface ProcessContext<P extends Pool<?>> {

	P getPool();
	Swimlane<P> getLane();
	
}
