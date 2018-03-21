package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.util.activity.REndPoint;

@ActivityDescription(lane = S1Lane1.class, outputs = {})
public class S1TheEnd2 extends REndPoint<S1Pool> {

	@Override
	public void doExecuteActivity() throws Exception {
		getContext().getPCase().close(2, "end2");
		super.doExecuteActivity();
	}

}
