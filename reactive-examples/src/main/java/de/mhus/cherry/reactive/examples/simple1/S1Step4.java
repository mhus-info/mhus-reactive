package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.ActorAssign;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.cherry.reactive.util.activity.RHumanTask;
import de.mhus.cherry.reactive.util.activity.RServiceTask;
import de.mhus.cherry.reactive.util.activity.RTask;

@ActivityDescription(
		outputs=@Output(activity=S1TheEnd.class),
		triggers={
				@Trigger(type=TYPE.MESSAGE,event="message",activity=S1TheEnd2.class),
				@Trigger(type=TYPE.SIGNAL,event="signal",activity=S1TheEnd2.class)
		}
		)
@ActorAssign(S1ActorSpecialist.class)
public class S1Step4 extends RHumanTask<S1Pool> {

	@Override
	public String doExecute() {
		return null;
	}

	@Override
	public HumanForm createForm() {
		return null;
	}

}
