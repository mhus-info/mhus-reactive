package de.mhus.cherry.reactive.examples.simple1.exclusive;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1Terminate2;
import de.mhus.cherry.reactive.examples.simple1.S1Terminate3;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RExclusiveGateway;

@ActivityDescription(
		outputs={
				@Output(activity=S1TheEnd.class,condition=S1ConditionSpock.class),
				@Output(activity=S1Terminate2.class,condition=S1ConditionKirk.class),
				@Output(activity=S1Terminate3.class)
		}
		)
public class S1GatewayExclusive extends RExclusiveGateway<S1Pool> {

}
