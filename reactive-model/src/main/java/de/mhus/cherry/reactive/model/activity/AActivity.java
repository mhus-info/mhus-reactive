package de.mhus.cherry.reactive.model.activity;

import java.util.Map;

import de.mhus.cherry.reactive.model.engine.ProcessContext;

public interface AActivity<P extends APool<?>> extends AElement<P> {

	ProcessContext<P> getContext();
	
	default P getPool() {
		return getContext().getPool();
	}

	void initializeActivity() throws Exception;
	
	void doExecuteActivity() throws Exception;

	Map<String, Object> exportParamters();
	
	void importParameters(Map<String, Object> parameters);

}
