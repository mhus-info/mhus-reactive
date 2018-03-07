package de.mhus.cherry.reactive.model.engine;

import java.util.List;

import de.mhus.cherry.reactive.model.activity.Element;

public interface ProcessLoader {

	List<Class<? extends Element>> getElements();
		
}
