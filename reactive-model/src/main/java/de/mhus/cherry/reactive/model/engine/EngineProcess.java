package de.mhus.cherry.reactive.model.engine;

import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.RElement;

public interface EngineProcess {

	String getProcessName();

	String getVersion();

	List<Class<? extends RElement<?>>> getElements();

	EnginePool getPool(String name);

	EngineElement getElement(String name);

	Set<String> getPoolNames();

	Set<String> getElementNames();

}
