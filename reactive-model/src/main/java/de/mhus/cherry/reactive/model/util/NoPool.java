package de.mhus.cherry.reactive.model.util;

import java.util.Map;

import de.mhus.cherry.reactive.model.activity.Pool;

@SuppressWarnings("rawtypes")
public class NoPool implements Pool {

	@Override
	public Map exportParamters() {
		return null;
	}

	@Override
	public void importParameters(Map parameters) {
	}

	@Override
	public void initializeCase(Map parameters) {
	}

	@Override
	public void closeCase() {
	}

}
