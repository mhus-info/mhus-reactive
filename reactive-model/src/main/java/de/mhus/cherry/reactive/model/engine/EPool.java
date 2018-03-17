package de.mhus.cherry.reactive.model.engine;

import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.annotations.PoolDescription;

public interface EPool {

	List<EElement> getStartPoints();

	Class<? extends APool<?>> getPoolClass();

	EElement getElement(String name);

	Set<String> getElementNames();

	List<EElement> getElements(Class<? extends AElement<?>> ifc);

	String getCanonicalName();

	List<EElement> getOutputElements(EElement element);

	String getName();

	boolean isElementOfPool(EElement element);

	PoolDescription getPoolDescription();
}
