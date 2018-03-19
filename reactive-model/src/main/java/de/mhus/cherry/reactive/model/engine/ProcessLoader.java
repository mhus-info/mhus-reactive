package de.mhus.cherry.reactive.model.engine;

import java.util.List;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;

public interface ProcessLoader {

	/**
	 * Return all found classes that implements AElement.
	 * 
	 * @return
	 */
	List<Class<? extends AElement<?>>> getElements();
	
	/**
	 * Return the process canonical name "class name:version"
	 * or null if not possible.
	 * 
	 * @return The process canonical name
	 */
	default String getProcessCanonicalName() {
		try {
			for (Class<? extends AElement<?>> clazz : getElements()) {
				if (AProcess.class.isAssignableFrom(clazz)) {
					// EngineUtil
					ProcessDescription desc = clazz.getAnnotation(ProcessDescription.class);
					if (desc != null) {
						return clazz.getCanonicalName() + ":" + desc.version();
					}
				}
			}
		} catch (Throwable t) {}
		return null;
	}
	
}
