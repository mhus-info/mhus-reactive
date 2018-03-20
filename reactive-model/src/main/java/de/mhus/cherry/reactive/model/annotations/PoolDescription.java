package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.Actor;
import de.mhus.cherry.reactive.model.util.EverybodyActor;

@Retention(RetentionPolicy.RUNTIME)
public @interface PoolDescription {

	String name() default "";
	String description() default "";
	String displayName() default "";
	

	Class<? extends Actor> actorDefault() default EverybodyActor.class;
	Class<? extends Actor>[] actorInitiator() default EverybodyActor.class;
	Class<? extends Actor>[] actorRead() default {};
	Class<? extends Actor>[] actorWrite() default {};

}
