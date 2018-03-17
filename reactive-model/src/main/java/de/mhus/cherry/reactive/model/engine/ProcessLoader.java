package de.mhus.cherry.reactive.model.engine;

import java.util.List;

import de.mhus.cherry.reactive.model.activity.AElement;

public interface ProcessLoader {

	List<Class<? extends AElement<?>>> getElements();
		
}
