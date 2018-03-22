package de.mhus.cherry.reactive.examples.simple1.parallel;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RGatewayJoin;

@ActivityDescription(
		outputs=@Output(activity=S1TheEnd.class)
		)
public class S1GatewayJoin extends RGatewayJoin<S1Pool>{

}
