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

import de.mhus.cherry.reactive.model.activity.AEndPoint;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.util.activity.RActivity;

public class REndPoint<P extends RPool<?>> extends RActivity<P> implements AEndPoint<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		// only if the last one with this runtime
//		getContext().getPRuntime().setState(STATE.CLOSED);
//		getContext().saveRuntime();
		getContext().getPNode().setState(STATE_NODE.CLOSED);
	}

}