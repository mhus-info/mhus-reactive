package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.Trigger;

public class ActivityUtil {

	public static Class<? extends AActivity<?>> getOutputByName(AActivity<?> element, String name) {
		return getOutputByName(element.getClass(), name);
	}
	
	@SuppressWarnings("rawtypes")
	public static Class<? extends AActivity<?>> getOutputByName(Class<? extends AActivity> element, String name) {
		ActivityDescription desc = element.getAnnotation(ActivityDescription.class);
		if (desc == null) return null;
		for (Output output : desc.outputs())
			if (name.equals(output.name()))
				return output.activity();
		return null;
	}

	public static String getEvent(AActivity<?> element) {
		return getEvent(element.getClass());
	}

	@SuppressWarnings("rawtypes")
	public static String getEvent(Class<? extends AActivity> element) {
		ActivityDescription desc = element.getAnnotation(ActivityDescription.class);
		if (desc == null || desc.event().length() == 0) return null;
		return desc.event();
	}

	public static Trigger[] getTriggers(AActivity<?> element) {
		return getTriggers(element.getClass());
	}
	
	@SuppressWarnings("rawtypes")
	public static Trigger[] getTriggers(Class<? extends AActivity> element) {
		ActivityDescription desc = element.getAnnotation(ActivityDescription.class);
		if (desc == null || desc.triggers() == null) return new Trigger[0];
		return desc.triggers();
	}

}
