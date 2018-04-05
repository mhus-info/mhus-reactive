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
package de.mhus.cherry.reactive.examples.simple1;

import java.util.Date;
import java.util.Map;

import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.cherry.reactive.util.bpmn2.RPool;

@PoolDescription(
		displayName="Example Pool",
		description="This pool is used to test the current development",
		indexDisplayNames = {"Text 1","Text 2","Created"},
		actorRead=S1ActorManager.class
		)
public class S1Pool extends RPool<S1Pool> {

	@PropertyDescription(displayName="Switch Text", writable = false )
	private String text1 = "Moin";
	@PropertyDescription
	private String text2 = "";

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	@Override
	protected void checkInputParameters(Map<String, Object> parameters) throws Exception {
		
	}

	public Object getText2() {
		return text2;
	}

	@Override
	public String[] createIndexValues(boolean init) {
		if (init)
			return new String[] {text1,text2,new Date().toString()};
		return null;
	}
		
	
}
