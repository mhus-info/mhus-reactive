package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RStartPoint;

@ActivityDescription(
		outputs=@Output(activity=S1StepMain.class),
		lane = S1Lane1.class,
		displayName="Start Point",
		description="The default start point for S1Pool"
		)
public class S1Start1 extends RStartPoint<S1Pool> {

}
