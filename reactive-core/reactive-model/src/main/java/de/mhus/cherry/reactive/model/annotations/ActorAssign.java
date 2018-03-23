package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.AActor;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActorAssign {
	Class<? extends AActor> value();
}
