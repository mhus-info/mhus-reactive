package de.mhus.cherry.reactive.model.activity;

import java.util.Map;

import de.mhus.cherry.reactive.model.util.IndexValuesProvider;

public interface APool<P extends APool<?>> extends AElement<P>, IndexValuesProvider {

	Map<String, Object> exportParamters();
	
	void importParameters(Map<String, Object> parameters);
	
	void initializeCase(Map<String, Object> parameters) throws Exception;
	
	void closeCase();

}
