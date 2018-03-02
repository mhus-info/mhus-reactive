package de.mhus.cherry.reactive.model.engine;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import de.mhus.lib.errors.NotFoundException;

/**
 * 
 * @author mikehummel
 *
 */
public interface StorageProvider {

	/**
	 * Save or update the case into storage.
	 * 
	 * @param caze
	 * @throws IOException 
	 */
	void saveCase(ReactiveCase caze) throws IOException;
	
	/**
	 * Load the case from storage'
	 * @param id
	 * @return The requested case
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	ReactiveCase loadCase(UUID id) throws IOException, NotFoundException;
	
	/**
	 * Delete the case and all flow data from storage.
	 * 
	 * @param id
	 * @throws IOException 
	 */
	void deleteCaseAndFlowNodes(UUID id) throws IOException;
	
	/**
	 * Save or update the flow node.
	 * 
	 * @param flow
	 * @throws IOException 
	 */
	void saveFlowNode(ReactiveFlowNode flow) throws IOException;
	
	/**
	 * Load the flow node.
	 * 
	 * @param id
	 * @return The requested node
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	ReactiveFlowNode loadFlowNode(UUID id) throws IOException,NotFoundException;

	/**
	 * Load all cases with the specified state or all. 
	 * 
	 * The set is only used to
	 * iterate the result. Other functionality is not needed. You can
	 * use a open database handle until the end of the queue is reached.
	 * 
	 * @param state The state or null for all states.
	 * @return Set to iterate the results.
	 * @throws IOException 
	 */
	Set<ReactiveCase> getCases(ReactiveCase.STATE state) throws IOException;
	
	/**
	 * Load all flows for this case with the specified state or all. 
	 * 
	 * The set is only used to
	 * iterate the result. Other functionality is not needed. You can
	 * use a open database handle until the end of the queue is reached.
	 * 
	 * @param caseId The id of the case.
	 * @param state The state or null for all states.
	 * @return list
	 * @throws IOException 
	 */
	Set<ReactiveFlowNode> getFlowNodes(UUID caseId, ReactiveFlowNode.STATE state) throws IOException;
	
	/**
	 * Return all flows with specified state or all. 
	 * 
	 * The set is only used to
	 * iterate the result. Other functionality is not needed. You can
	 * use a open database handle until the end of the queue is reached.
	 * 
	 * @param state The state or null for all
	 * @return Set of all nodes
	 * @throws IOException 
	 */
	Set<ReactiveFlowNode> getFlowNodes(ReactiveFlowNode.STATE state) throws IOException;
	
}
