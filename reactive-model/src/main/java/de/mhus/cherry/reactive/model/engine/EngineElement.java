package de.mhus.cherry.reactive.model.engine;

import java.util.List;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.RElement;
import de.mhus.cherry.reactive.model.activity.Swimlane;
import de.mhus.cherry.reactive.model.annotations.Trigger;

public interface EngineElement {

	Class<? extends RElement<?>> getElementClass();

	String getCanonicalName();

	@SuppressWarnings("rawtypes")
	boolean is(Class<? extends RElement> ifc);

	Class<? extends Activity<?>>[] getOutputs();

	Trigger[] getTriggers();

	Class<? extends Swimlane<?>> getSwiminglane();

}
