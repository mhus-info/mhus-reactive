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

import java.util.LinkedList;

import de.mhus.cherry.reactive.model.activity.ACondition;
import de.mhus.cherry.reactive.model.activity.AInclusiveGateway;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.model.util.NoCondition;
import de.mhus.cherry.reactive.util.activity.RGateway;
import de.mhus.lib.errors.MException;

/**
 * Collect all output where the condition is zero or higher.
 * If no condition is found the default output will be executed.
 * If no default output was found an exception is thrown.
 * This gateway can execute one ore more outputs in parallel.
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public class RInclusiveGateway<P extends RPool<?>> extends RGateway<P> implements AInclusiveGateway<P> {

	@SuppressWarnings("unchecked")
	@Override
	public Output[] doExecute() throws Exception {
		Output defaultOutput = null;
		LinkedList<Output> successful = new LinkedList<>();
		for (Output output : ActivityUtil.getOutputs(this)) {
			if (output.condition() == NoCondition.class) {
				defaultOutput = output;
			} else {
				Class<? extends ACondition<P>> condition = (Class<? extends ACondition<P>>) output.condition();
				int res = condition.newInstance().check(getContext());
				if (res >= 0) {
					successful.add(output);
				}
			}
		}
		if (successful.size() == 0 && defaultOutput != null) successful.add(defaultOutput);
		if (successful.size() == 0) throw new MException("condition not found",getClass().getCanonicalName());
		return successful.toArray(new Output[successful.size()]);
	}

}
