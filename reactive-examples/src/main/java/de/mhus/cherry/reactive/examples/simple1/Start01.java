package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.StartPoint;
import de.mhus.cherry.reactive.model.annotations.ActivityDefinition;

@ActivityDefinition(
		outputs=FirstStep01.class,
		lane = MyLane01.class
		)
public class Start01 implements StartPoint {

}
