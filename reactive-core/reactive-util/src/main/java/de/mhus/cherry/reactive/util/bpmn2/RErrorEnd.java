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
package de.mhus.cherry.reactive.util.bpmn2;

import java.util.UUID;

import de.mhus.cherry.reactive.model.activity.AEndPoint;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.util.activity.RActivity;

/**
 * The end will trigger an error on a parent process but will not terminate the current case.
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public abstract class RErrorEnd<P extends RPool<?>> extends RActivity<P> implements AEndPoint<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		UUID closeActivity = getContext().getPCase().getCloseActivity();
		if (closeActivity == null) return;
		
		PNode closeNode = getContext().getEEngine().getFlowNode(closeActivity);
		if (closeNode.getState() != STATE_NODE.WAITING) {
			getContext().getARuntime().doErrorMsg(getContext().getPNode(), "closeActivity is in wrong state",closeNode.getState());
			return;
		}
		
		((InternalEngine)getContext().getEEngine()).doNodeErrorHandling(closeNode, getError());
		
	}

	protected abstract String getError();

}
