package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.ActorAssign;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RServiceTask;

@ActivityDescription(
		outputs=@Output(activity=S1TheEnd.class),
		lane = S1Lane1.class
		)
@ActorAssign(S1ActorSpecialist.class)
public class S1Step3 extends RServiceTask<S1Pool> {

	@Override
	public Class<? extends AActivity<S1Pool>> doExecute() {
		return S1TheEnd.class;
	}

}
