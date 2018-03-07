package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.StartPoint;
import de.mhus.cherry.reactive.model.migrate.Migrator;

@Retention(RetentionPolicy.RUNTIME)
public @interface PoolDescription {

	String name() default "";
	Class<? extends StartPoint<?>>[] startPoints();
	
}
