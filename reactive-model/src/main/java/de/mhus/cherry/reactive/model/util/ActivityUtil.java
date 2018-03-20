package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;

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
}
