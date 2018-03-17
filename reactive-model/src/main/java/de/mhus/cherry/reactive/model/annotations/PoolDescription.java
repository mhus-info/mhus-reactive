package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.util.EverybodyActor;

@Retention(RetentionPolicy.RUNTIME)
public @interface PoolDescription {

	String name() default "";
	String description() default "";

	Class<? extends AActor<?>> actorDefault() default EverybodyActor.class;
	Class<? extends AActor<?>>[] actorInitiator() default EverybodyActor.class;
	Class<? extends AActor<?>>[] actorRead() default {};
	Class<? extends AActor<?>>[] actorWrite() default {};

}
