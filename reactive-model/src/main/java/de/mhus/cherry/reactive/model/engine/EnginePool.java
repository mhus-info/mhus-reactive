package de.mhus.cherry.reactive.model.engine;

import de.mhus.cherry.reactive.model.activity.Pool;
import de.mhus.cherry.reactive.model.activity.StartPoint;

public interface EnginePool {

	Class<? extends StartPoint<?>>[] getStartPoints();

	Class<? extends Pool<?>> getElementClass();

}
