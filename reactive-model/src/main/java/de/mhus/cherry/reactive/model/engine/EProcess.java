package de.mhus.cherry.reactive.model.engine;

import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;

public interface EProcess {

	/**
	 * Return the simple name of the process.
	 * 
	 * @return Simple name
	 */
	String getName();

	String getVersion();

	List<Class<? extends AElement<?>>> getElements();

	EPool getPool(String name);

	EElement getElement(String name);

	Set<String> getPoolNames();

	Set<String> getElementNames();
	
	ProcessDescription getProcessDescription();

	/**
	 * Return processName : processVersion
	 * @return The unique name processName : processVersion
	 */
	String getProcessName();

	Class<? extends AProcess> getProcessClass();

	/**
	 * Return the canonical name of the process class only.
	 * 
	 * @return canonical class name
	 */
	String getCanonicalName();

}
