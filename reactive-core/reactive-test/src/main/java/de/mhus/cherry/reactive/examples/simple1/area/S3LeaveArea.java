package de.mhus.cherry.reactive.examples.simple1.area;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RLeaveRestrictedArea;

@ActivityDescription(/*event="onlyoneplease",*/ outputs = @Output(activity = S1TheEnd.class))
public class S3LeaveArea extends RLeaveRestrictedArea<S1Pool>{
    
}
