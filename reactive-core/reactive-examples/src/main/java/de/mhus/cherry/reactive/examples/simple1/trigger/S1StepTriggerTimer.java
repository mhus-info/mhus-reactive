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
import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.cherry.reactive.util.activity.RHumanTask;
import de.mhus.lib.errors.MException;

@ActivityDescription(
		outputs=@Output(activity=S1TheEnd.class),
		triggers={
				@Trigger(type=TYPE.TIMER,event="1s",activity=S1Terminate2.class),
		}
		)
@ActorAssign(S1ActorSpecialist.class)
public class S1StepTriggerTimer extends RHumanTask<S1Pool> {

	@Override
	public HumanForm createForm() {
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
