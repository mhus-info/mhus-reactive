package de.mhus.cherry.reactive.model.engine;

import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.RElement;
import de.mhus.cherry.reactive.model.activity.Pool;
import de.mhus.cherry.reactive.model.activity.StartPoint;

public interface EnginePool {

	List<EngineElement> getStartPoints();

	Class<? extends Pool<?>> getPoolClass();

	EngineElement getElement(String name);

	Set<String> getElementNames();

	List<EngineElement> getElements(Class<? extends RElement<?>> ifc);

	String getCanonicalName();

	List<EngineElement> getOutputElements(EngineElement element);

}
