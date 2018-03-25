package de.mhus.cherry.reactive.model.util;

import java.util.Map;

import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.lib.errors.NotSupportedException;

@SuppressWarnings("rawtypes")
public class NoPool implements APool<NoPool> {

	@Override
	public Map<String, Object> exportParamters() {
		throw new NotSupportedException();
	}

	@Override
	public void importParameters(Map parameters) {
		throw new NotSupportedException();
	}

	@Override
	public void initializeCase(Map parameters) {
		throw new NotSupportedException();
	}

	@Override
	public void closeCase() {
		throw new NotSupportedException();
	}

	@Override
	public String[] createIndexValues(boolean init) {
		return null;
	}

}
