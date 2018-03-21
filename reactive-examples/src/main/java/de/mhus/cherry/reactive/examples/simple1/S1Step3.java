package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.ActorAssign;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RServiceTask;
import de.mhus.cherry.reactive.util.activity.RTask;

@ActivityDescription(
		outputs=@Output(name=S1Step3.END,activity=S1TheEnd.class),
		lane = S1Lane1.class
		)
@ActorAssign(S1ActorSpecialist.class)
public class S1Step3 extends RServiceTask<S1Pool> {

	public static final String END = "end";
	@Override
	public String doExecute() {
		return END;
	}

}
