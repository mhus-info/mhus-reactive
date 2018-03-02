package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.Swimlane;
import de.mhus.cherry.reactive.model.util.NoActivity;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityDescription {

	public Class<? extends Activity<?>>[] outputs();
	public Class<? extends Activity<?>>[] errorTriggers() default NoActivity.class;
	public Class<? extends Activity<?>> defaultErrorTrigger() default NoActivity.class;
	public String timer() default "";
	public Class<? extends Activity<?>> timerTrigger() default NoActivity.class;
	public Class<? extends Swimlane<?>> lane();
	
}
