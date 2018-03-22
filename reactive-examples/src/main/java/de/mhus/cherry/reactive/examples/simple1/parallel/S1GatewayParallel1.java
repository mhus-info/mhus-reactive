package de.mhus.cherry.reactive.examples.simple1.parallel;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RGatewayParallel;

@ActivityDescription(
		outputs={
				@Output(activity=S1Parallel1Way1.class),
				@Output(activity=S1Parallel1Way2.class)
		}
		)
public class S1GatewayParallel1 extends RGatewayParallel<S1Pool>{

}
