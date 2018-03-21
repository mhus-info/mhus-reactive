package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RExternalEvent;
import de.mhus.cherry.reactive.util.activity.RMessageEvent;
import de.mhus.cherry.reactive.util.activity.RServiceTaskInternal;

@ActivityDescription(
		event = "message",
		outputs = @Output(activity=S1TheEnd.class), 
		lane = S1Lane1.class
		)
public class S1EventMessage extends RMessageEvent<S1Pool> {

	@Override
	public void doExecute() throws Exception {
		log().i(getContext().getPNode().getMessage());
	}

}
