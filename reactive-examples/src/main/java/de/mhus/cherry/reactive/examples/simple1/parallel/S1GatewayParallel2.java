package de.mhus.cherry.reactive.examples.simple1.parallel;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RParallelGateway;

@ActivityDescription(
		outputs={
				@Output(activity=S1Parallel2Way1.class),
				@Output(activity=S1Parallel2Way2.class)
		}
		)
public class S1GatewayParallel2 extends RParallelGateway<S1Pool>{

}
