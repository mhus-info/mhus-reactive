package de.mhus.cherry.reactive.model.engine;

import java.util.HashMap;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.Trigger;

public interface EElement {

	Class<? extends AElement<?>> getElementClass();

	String getCanonicalName();

	@SuppressWarnings("rawtypes")
	boolean is(Class<? extends AElement> ifc);

	Output[] getOutputs();

	Trigger[] getTriggers();

	Class<? extends ASwimlane<?>> getSwimlane();

	String getName();

	boolean isInterface(Class<?> ifc);

	ActivityDescription getActivityDescription();

	HashMap<String, Long> getSchedulerList();

	HashMap<String, String> getSignalList();

	HashMap<String, String> getMessageList();

}
