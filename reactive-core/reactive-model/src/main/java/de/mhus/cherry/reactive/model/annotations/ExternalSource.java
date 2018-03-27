package de.mhus.cherry.reactive.model.annotations;

public @interface ExternalSource {

	String name();
	String provider();
	String scope() default "";
	
}
