package de.mhus.cherry.reactive.model.engine;

public interface Result<T> extends Iterable<T> {

	void close();
	
}
