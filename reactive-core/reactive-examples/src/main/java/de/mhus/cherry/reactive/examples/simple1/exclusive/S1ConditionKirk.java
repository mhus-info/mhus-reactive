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
package de.mhus.cherry.reactive.examples.simple1.exclusive;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.model.activity.ACondition;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class S1ConditionKirk implements ACondition<S1Pool> {

	@Override
	public int check(ProcessContext<S1Pool> context) {
		S1Pool pool = context.getPool();
		return pool.getText2().equals("kirk") ? TRUE : FALSE;
	}

}
