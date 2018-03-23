package de.mhus.cherry.reactive.examples.simple1.events;

import de.mhus.cherry.reactive.examples.simple1.S1Lane1;
import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RExternalEvent;

@ActivityDescription(
		outputs = @Output(activity=S1TheEnd.class), 
		lane = S1Lane1.class
		)
public class S1EventExternal extends RExternalEvent<S1Pool> {

	@Override
	public void doExecute() throws Exception {
		log().i(getContext().getPNode().getMessage());
	}

}
