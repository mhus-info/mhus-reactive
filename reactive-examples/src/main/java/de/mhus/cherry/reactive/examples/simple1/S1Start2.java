package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.InactiveStartPoint;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.util.ReactiveStartPoint;

@ActivityDescription(
		outputs=S1Step1.class,
		lane = S1Lane1.class
		)
public class S1Start2 extends ReactiveStartPoint<S1Pool2> implements InactiveStartPoint {

}
