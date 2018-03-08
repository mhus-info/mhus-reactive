package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.util.ReactiveEndPoint;

@ActivityDescription(lane = S1Lane1.class, outputs = {})
public class S1TheEnd extends ReactiveEndPoint<S1Pool> {

}
