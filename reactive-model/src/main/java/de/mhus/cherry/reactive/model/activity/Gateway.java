package de.mhus.cherry.reactive.model.activity;

import java.util.Map;

public interface Gateway<P extends Pool<?>> extends Activity<P> {

	Class<? extends Activity<P>>[] doExecute() throws Exception;

	Map<String, Object> exportParamters();
	
	void importParameters(Map<String, Object> parameters);

}
