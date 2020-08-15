package de.mhus.cherry.reactive.examples.simple1.area;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.REnterRestrictedArea;

@ActivityDescription(event = "onlyoneplease", outputs = @Output(activity = S2DoSomething.class))
public class S1EnterArea extends REnterRestrictedArea<S1Pool> {}
