package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.util.EverybodyActor;

@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyDescription {

	String name() default "";
	String description() default "";
	String displayName() default "";

}
