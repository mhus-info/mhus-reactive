package de.mhus.cherry.reactive.util.engine;

import java.util.LinkedList;

import de.mhus.cherry.reactive.model.engine.Result;

public class ResultList<T> extends LinkedList<T> implements Result<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public void close() {
	}

}
