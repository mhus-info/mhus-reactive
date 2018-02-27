package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.Lane;
import de.mhus.cherry.reactive.model.util.NoActivity;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityDefinition {

	public Class<? extends Activity>[] outputs();
	public Class<? extends Activity>[] errors() default NoActivity.class;
	public String timeout() default "";
	public Class<? extends Activity> timeoutActivity() default NoActivity.class;
	public Class<? extends Lane> lane();
	
}
