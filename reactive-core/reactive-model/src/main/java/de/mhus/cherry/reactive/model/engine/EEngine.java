package de.mhus.cherry.reactive.model.engine;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.lib.errors.NotFoundException;

public interface EEngine {

	Result<PCaseInfo> storageGetCases(STATE_CASE state) throws IOException;

	Result<PNodeInfo> storageGetFlowNodes(UUID caseId, STATE_NODE state) throws IOException;

	Result<PNodeInfo> storageGetAssignedFlowNodes(String user) throws IOException;

	Result<PNodeInfo> storageGetScheduledFlowNodes(STATE_NODE state, long scheduled) throws IOException;

	Result<PNodeInfo> storageGetSignaledFlowNodes(STATE_NODE state, String signal) throws IOException;

	Result<PNodeInfo> storageGetMessageFlowNodes(UUID caseId, STATE_NODE state, String message) throws IOException;

	PNode getFlowNode(UUID nodeId) throws NotFoundException, IOException;

	void saveFlowNode(PNode flow) throws IOException, NotFoundException;

}
