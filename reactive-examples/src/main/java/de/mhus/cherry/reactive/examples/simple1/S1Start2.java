package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.util.InactiveStartPoint;
import de.mhus.cherry.reactive.util.activity.RStartPoint;

@ActivityDescription(
		outputs=@Output(activity=S1Step2.class),
		lane = S1Lane1.class
		)
public class S1Start2 extends RStartPoint<S1Pool> implements InactiveStartPoint {

}
