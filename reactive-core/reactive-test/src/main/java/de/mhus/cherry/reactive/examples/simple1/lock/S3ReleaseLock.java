package de.mhus.cherry.reactive.examples.simple1.lock;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RReleaseLock;

@ActivityDescription(/*event="onlyoneplease",*/ outputs = @Output(activity = S1TheEnd.class))
public class S3ReleaseLock extends RReleaseLock<S1Pool>{
    
}
