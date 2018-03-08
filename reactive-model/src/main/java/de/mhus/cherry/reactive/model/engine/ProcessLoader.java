package de.mhus.cherry.reactive.model.engine;

import java.util.List;

import de.mhus.cherry.reactive.model.activity.RElement;

public interface ProcessLoader {

	List<Class<? extends RElement<?>>> getElements();
		
}
