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

@ActivityDescription(
		outputs=@Output(activity=S1TheEnd.class),
		triggers={
				@Trigger(type=TYPE.MESSAGE,event="message",activity=S1Terminate2.class),
				@Trigger(type=TYPE.SIGNAL,event="signal",activity=S1Terminate2.class)
		}
		)
@ActorAssign(S1ActorSpecialist.class)
public class S1StepTrigger extends RHumanTask<S1Pool> {

	@Override
	public String doExecute() {
		return null;
	}

	@Override
	public HumanForm createForm() {
		return null;
	}

	@Override
	public String[] createIndexValues(boolean init) {
		return null;
	}

}
