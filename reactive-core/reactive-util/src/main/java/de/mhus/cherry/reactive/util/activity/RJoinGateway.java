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
/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.mhus.cherry.reactive.util.activity;

import java.util.LinkedList;
import java.util.UUID;

import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.engine.EEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.util.ActivityUtil;

/**
 * Wait for all inputs and then execute all Outputs in parallel.
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public class RJoinGateway<P extends RPool<?>> extends RGateway<P> {

	@Override
	public void initializeActivity() throws Exception {

		ProcessContext<P> context = getContext();
		EEngine engine = context.getEEngine();
		// by default set to wait
		context.getPNode().setState(STATE_NODE.WAITING);
		
		// all inputs are done?
		int size = ActivityUtil.getInputs(this).length;
		
		// find waiting gateways
		String myName = context.getPNode().getCanonicalName();
		UUID myId = context.getPNode().getId();
		LinkedList<UUID> current = new LinkedList<>();
		for ( PNodeInfo info : engine.storageGetFlowNodes(context.getPNode().getCaseId(), STATE_NODE.WAITING)) {
			if (info.getCanonicalName().equals(myName) && !info.getId().equals(myId) /* paranoia */) 
				current.add(info.getId());
		}
		
		if (current.size() >= (size-1) ) { // decrease 1 for myself
			// close all others, activate me !!!
			context.getPNode().setState(STATE_NODE.RUNNING);
			for (UUID id : current) {
				PNode node = engine.getFlowNode(id);
				node.setState(STATE_NODE.CLOSED);
				engine.saveFlowNode(node);
			}
		}
		
	}

	
	@Override
	public Output[] doExecute() throws Exception {
		return ActivityUtil.getOutputs(this);
	}

}
