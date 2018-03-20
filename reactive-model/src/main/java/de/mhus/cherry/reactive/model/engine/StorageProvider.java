package de.mhus.cherry.reactive.model.engine;

import java.io.IOException;
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
	void saveCase(PCase caze) throws IOException;
	
	/**
	 * Load the case from storage'
	 * @param id
	 * @return The requested case
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	PCase loadCase(UUID id) throws IOException, NotFoundException;
	
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
	void saveFlowNode(PNode flow) throws IOException;
	
	/**
	 * Load the flow node.
	 * 
	 * @param id
	 * @return The requested node
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	PNode loadFlowNode(UUID id) throws IOException,NotFoundException;

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
	Result<PCaseInfo> getCases(PCase.STATE_CASE state) throws IOException;
	
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
	Result<PNodeInfo> getFlowNodes(UUID caseId, PNode.STATE_NODE state) throws IOException;
	
	/**
	 * Returns all flow nodes with the state and a scheduled time greater zero and
	 * lesser or equals 'scheduled'.
	 * 
	 * @param state The state or null
	 * @param scheduled
	 * @return List of results
	 * @throws IOException
	 */
	Result<PNodeInfo> getScheduledFlowNodes(PNode.STATE_NODE state, long scheduled) throws IOException;
	
	Result<PNodeInfo> getSignaledFlowNodes(PNode.STATE_NODE state, String signal) throws IOException;
	
	Result<PNodeInfo> getMessageFlowNodes(UUID caseId, PNode.STATE_NODE state, String message) throws IOException;

	Result<PNodeInfo> getAssignedFlowNodes(String user) throws IOException;
	
	/**
	 * Return new engine persistence status. If no engine status is stored return null.
	 * @return List of results
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	PEngine loadEngine() throws IOException, NotFoundException;
	
	void saveEngine(PEngine engine) throws IOException;
	
}
