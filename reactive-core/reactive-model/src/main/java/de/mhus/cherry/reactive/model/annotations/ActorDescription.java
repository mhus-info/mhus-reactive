package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActorDescription {
	String name() default "";
	String description() default "";
	String[] groups() default {};
	String[] users() default {};
}
