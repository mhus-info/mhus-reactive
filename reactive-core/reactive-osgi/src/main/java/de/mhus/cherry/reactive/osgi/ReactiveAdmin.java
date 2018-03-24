package de.mhus.cherry.reactive.osgi;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.util.PoolValidator.Finding;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.lib.errors.MException;

public interface ReactiveAdmin {

	enum STATE_ENGINE {STOPPED,SUSPENDED,RUNNING}
	
	void startEngine();

	void stopEngine();

	Collection<String> getAvailableProcesses();

	boolean addProcess(String name, ProcessLoader loader);

	void removeProcess(String name);

	List<Finding> deploy(String name, boolean addVersion, boolean activate) throws MException;

	void undeploy(String name) throws MException;

	String getProcessDeployName(String name);

	Engine getEngine();

	PEngine getEnginePersistence();

	void setExecutionSuspended(boolean suspend);

	STATE_ENGINE getEngineState();

	String getProcessInfo(String name);

	String addProcess(String[] fileNames, boolean remember) throws FileNotFoundException;

}
