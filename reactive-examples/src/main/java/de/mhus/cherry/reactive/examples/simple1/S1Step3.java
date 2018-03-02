package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.ActorAssign;
import de.mhus.cherry.reactive.util.ReactiveServiceTask;

@ActivityDescription(
		outputs=S1TheEnd.class,
		lane = S1Lane1.class
		)
@ActorAssign(S1ActorSpecialist.class)
public class S1Step3 extends ReactiveServiceTask<S1ExamplePool> {

	@Override
	public Class<? extends Activity<S1ExamplePool>> doExecute() {
		return S1TheEnd.class;
	}

}
