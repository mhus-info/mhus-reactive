package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.migrate.Migrator;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessDescription {
	String version();
	String name() default "";
	String description() default "";
	
	Class<? extends Migrator>[] migrator() default {};
	String defaultPool() default "";
	
}
