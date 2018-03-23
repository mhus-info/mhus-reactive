package de.mhus.cherry.reactive.model.activity;

import java.util.Map;

public interface APool<P extends APool<?>> extends AElement<P> {

	Map<String, Object> exportParamters();
	
	void importParameters(Map<String, Object> parameters);
	
	void initializeCase(Map<String, Object> parameters) throws Exception;
	
	void closeCase();

}
