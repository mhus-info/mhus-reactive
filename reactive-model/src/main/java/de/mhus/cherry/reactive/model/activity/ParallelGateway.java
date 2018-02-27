package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.annotations.ActivityDefinition;

public class ParallelGateway extends Gateway {

	@Override
	public Class<? extends Activity> doExecute() throws Exception {
		ActivityDefinition actDef = getClass().getAnnotation(ActivityDefinition.class);
		return null;
	}

}
