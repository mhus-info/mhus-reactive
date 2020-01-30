package de.mhus.cherry.reactive.examples.simple1.lock;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RAquireLock;

@ActivityDescription(event = "onlyoneplease",outputs = @Output(activity = S2DoSomething.class))
public class S1AquireLock extends RAquireLock<S1Pool>{

}
