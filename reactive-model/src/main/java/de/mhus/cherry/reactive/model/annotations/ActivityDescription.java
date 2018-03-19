package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.cherry.reactive.model.util.DefaultSwimlane;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityDescription {

//	public Class<? extends AActivity<?>>[] outputs();
	public Output[] outputs() default {};
	public Trigger[] triggers() default {};
	public Class<? extends ASwimlane<?>> lane() default DefaultSwimlane.class;
	String description() default "";
	String caption() default "";
	public Class<? extends RuntimeNode> runtime() default RuntimeNode.class;
	public String name() default "";
}
