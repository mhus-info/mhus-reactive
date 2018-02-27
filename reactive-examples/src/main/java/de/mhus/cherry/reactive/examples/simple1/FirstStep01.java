package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.ServiceActivity;
import de.mhus.cherry.reactive.model.activity.TaskException;
import de.mhus.cherry.reactive.model.annotations.ActivityDefinition;
import de.mhus.lib.annotations.adb.DbPersistent;

@ActivityDefinition(
		outputs = {SecondStep01.class,ThirdStep01.class},
		lane = MyLane01.class
		)
public class FirstStep01 extends ServiceActivity {

	private String localText;
	
	@Override
	public Class<? extends Activity> doExecute()  throws Exception {
		
		switch( ((ExamplePool01)context.getPool()).getText1() ) {
		case "error1":
			throw new TaskException("Mist", TheEnd01.class);
		case "fatal":
			throw new Exception("Mist");
		case "second":
			return SecondStep01.class;
		case "third":
			return ThirdStep01.class;
		}
		return null;
	}

}
