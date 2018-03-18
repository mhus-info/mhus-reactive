package de.mhus.cherry.reactive.osgi;

import java.util.Collection;
import java.util.List;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.PoolValidator.Finding;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.lib.errors.MException;

public interface ReactiveAdmin {

	void startEngine();

	void stopEngine();

	boolean isEngineRunning();

	Collection<String> getAvailableProcesses();

	boolean addProcess(String name, ProcessLoader loader);

	void removeProcess(String name);

	List<Finding> deploy(String name) throws MException;

	void undeploy(String name) throws MException;

	String getProcessDeployName(String name);

	Engine getEngine();

}
