package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.AActivity;

@Retention(RetentionPolicy.RUNTIME)
public @interface Trigger {
	enum TYPE {ERROR,DEFAULT_ERROR,TIMER,MESSAGE,SIGNAL,NOOP};
	TYPE type();
	String name() default "";
	String description() default "";
	Class<? extends AActivity<?>> activity();
	String event() default "";
	boolean abord() default true;
	
}
