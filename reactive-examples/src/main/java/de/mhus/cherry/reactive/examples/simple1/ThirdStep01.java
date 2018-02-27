package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.ServiceActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDefinition;

@ActivityDefinition(
		outputs=TheEnd01.class,
		lane = MyLane01.class
		)
public class ThirdStep01 extends ServiceActivity {

	@Override
	public Class<? extends Activity> doExecute() {
		return TheEnd01.class;
	}

}
