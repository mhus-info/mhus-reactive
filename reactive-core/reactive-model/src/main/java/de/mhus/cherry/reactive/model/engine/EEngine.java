/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.model.engine;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.lib.errors.NotFoundException;

public interface EEngine {

	Result<PCaseInfo> storageSearchCases(SearchCriterias criterias) throws IOException;
	
	Result<PCaseInfo> storageGetCases(STATE_CASE state) throws IOException;

	Result<PNodeInfo> storageGetFlowNodes(UUID caseId, STATE_NODE state) throws IOException;

	Result<PNodeInfo> storageSearchFlowNodes(SearchCriterias criterias) throws IOException;

	Result<PNodeInfo> storageGetScheduledFlowNodes(STATE_NODE state, long scheduled) throws IOException;

	Result<PNodeInfo> storageGetSignaledFlowNodes(STATE_NODE state, String signal) throws IOException;

	Result<PNodeInfo> storageGetMessageFlowNodes(UUID caseId, STATE_NODE state, String message) throws IOException;

	PNode getFlowNode(UUID nodeId) throws NotFoundException, IOException;

	void saveFlowNode(PNode flow) throws IOException, NotFoundException;

}
