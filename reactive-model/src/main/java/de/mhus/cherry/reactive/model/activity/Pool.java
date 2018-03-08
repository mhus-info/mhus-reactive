package de.mhus.cherry.reactive.model.activity;

import java.util.Map;

/**
 * Case Lifecycle:
 * - start the new case
 * - constructor
 * - setContext()
 * - initializeCase() / checkInputParameters()
 * - getStartPoint()
 * - exportParamters()
 * - destroy
 * Loop:
 * - constructor
 * - setContext()
 * - importParameters()
 * - do task
 * - exportParamters()
 * - destroy
 * Ending:
 * - constructor
 * - setContext()
 * - importParameters()
 * - closeCase()
 * - exportParamters()
 * - destroy
 * - archive case
 * 
 * FlowNode Lifecycle:
 * - start or retry new task
 * - constructor
 * - setContext()
 * - initializeTask()
 * - doExecute()
 * - exportParamters()
 * - destroy
 * Loop (if needed):
 * - constructor
 * - setContext()
 * - importParameters()
 * - doExecute()
 * - exportParamters()
 * - destroy
 * @param <P> 
 * 
 */
public interface Pool<P extends Pool<?>> extends RElement<P> {

	Map<String, Object> exportParamters();
	
	void importParameters(Map<String, Object> parameters);
	
	void initializeCase(Map<String, Object> parameters) throws Exception;
	
	void closeCase();
		
}
