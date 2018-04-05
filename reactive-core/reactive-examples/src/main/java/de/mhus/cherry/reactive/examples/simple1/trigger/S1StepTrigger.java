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
package de.mhus.cherry.reactive.examples.simple1.trigger;

import de.mhus.cherry.reactive.examples.simple1.S1ActorSpecialist;
import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1Terminate2;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.ActorAssign;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.util.UserForm;
import de.mhus.cherry.reactive.util.bpmn2.RUserTask;
import de.mhus.lib.errors.MException;

@ActivityDescription(
		outputs=@Output(activity=S1TheEnd.class),
		triggers={
				@Trigger(type=TYPE.MESSAGE,event="message",activity=S1Terminate2.class),
				@Trigger(type=TYPE.SIGNAL,event="signal",activity=S1Terminate2.class)
		}
		)
@ActorAssign(S1ActorSpecialist.class)
public class S1StepTrigger extends RUserTask<S1Pool> {

	@Override
	public UserForm createForm() {
		return null;
	}

	@Override
	public String[] createIndexValues(boolean init) {
		return null;
	}

	@Override
	protected void doSubmit() throws MException {
		// TODO Auto-generated method stub
		
	}

}
