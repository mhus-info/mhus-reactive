package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RExclusiveGateway;

@ActivityDescription(
		outputs={
				@Output(activity=S1TheEnd.class,condition=S1ConditionSpock.class),
				@Output(activity=S1TheEnd2.class,condition=S1ConditionKirk.class)
		}
		)
public class S1ExclusiveGateway extends RExclusiveGateway<S1Pool> {

}
