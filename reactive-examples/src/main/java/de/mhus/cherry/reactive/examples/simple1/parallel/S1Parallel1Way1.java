package de.mhus.cherry.reactive.examples.simple1.parallel;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RServiceTask;

@ActivityDescription(
		outputs = @Output(activity=S1TheEnd.class)
		)
public class S1Parallel1Way1 extends RServiceTask<S1Pool> {

	@Override
	public String doExecute() throws Exception {
		return null;
	}

}
