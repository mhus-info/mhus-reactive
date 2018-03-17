package de.mhus.cherry.reactive.model.engine;

import java.util.HashMap;
import java.util.Map;

public class PEngine {

	protected Map<String,Object> parameters;

	public PEngine() {
		
	}
	public PEngine(PEngine clone) {
		parameters = new HashMap<>(clone.getParameters());
	}

	public Map<String, Object> getParameters() {
		if (parameters == null) parameters = new HashMap<>();
		return parameters;
	}

	@Override
	public String toString() {
		return "Engine: " + parameters;
	}

}
