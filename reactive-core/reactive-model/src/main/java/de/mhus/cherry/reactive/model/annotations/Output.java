package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.ACondition;
import de.mhus.cherry.reactive.model.util.NoCondition;

@Retention(RetentionPolicy.RUNTIME)
public @interface Output {
	String name() default "";
	String description() default "";
	Class<? extends ACondition<?>> condition() default NoCondition.class;
	Class<? extends AActivity<?>> activity();
}
