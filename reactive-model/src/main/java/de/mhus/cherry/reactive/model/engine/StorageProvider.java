package de.mhus.cherry.reactive.model.engine;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Iterate pending 
 * @author mikehummel
 *
 */
public interface StorageProvider {

	/**
	 * Save or update the case into storage.
	 * 
	 * @param caze
	 */
	void saveCase(ReactiveCase caze);
	
	/**
	 * Load the case from storage'
	 * @param id
	 * @return
	 */
	ReactiveCase loadCase(UUID id);
	
	/**
	 * Delete the case and all flow data from storage.
	 * 
	 * @param id
	 */
	void deleteCaseAndFlows(UUID id);
	
	/**
	 * Save or update the flow node.
	 * 
	 * @param flow
	 */
	void saveFlow(ReactiveFlow flow);
	
	/**
	 * Load the flow node.
	 * 
	 * @param id
	 * @return
	 */
	ReactiveFlow loadFlow(UUID id);

	/**
	 * Load all cases with the specified state or all. The set is only used to
	 * iterate the result. Other functionality is not needed. You can
	 * use a open database handle until the end of the queue is reached.
	 * 
	 * @param state The state or null for all states.
	 * @return Set to iterate the results.
	 */
	Set<ReactiveCase> getCases(ReactiveCase.STATE state);
	
	/**
	 * Load all flows for this case with the specified state or all.
	 * @param caseId The id of the case.
	 * @param state The state or null for all states.
	 * @return
	 */
	Set<ReactiveFlow> getFlows(UUID caseId, ReactiveFlow.STATE state);
	
	/**
	 * Return all Flows for the message.
	 * 
	 * @param message
	 * @return
	 */
	Set<ReactiveFlow> getFlowsForMessage(String message);
	
}
