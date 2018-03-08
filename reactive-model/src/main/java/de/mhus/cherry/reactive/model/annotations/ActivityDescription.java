package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.Swimlane;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.util.NoActivity;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityDescription {

	public Class<? extends Activity<?>>[] outputs();
	public Trigger[] triggers() default {};
	public Class<? extends Swimlane<?>> lane();
	String description() default "";
	String caption() default "";

}
