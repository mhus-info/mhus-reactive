package de.mhus.cherry.reactive.model.engine;

import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.Element;

public interface EngineProcess {

	String getProcessName();

	String getVersion();

	List<Class<? extends Element>> getElements();

	EnginePool getPool(String name);

	EngineActivity getActivity(String name);

	Set<String> getPoolNames();

	Set<String> getActivityNames();

}
