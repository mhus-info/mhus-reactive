package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyDescription {

	String name() default "";
	String description() default "";
	String displayName() default "";
	boolean writable() default true;

}
