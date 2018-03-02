package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.TaskException;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.util.ReactiveServiceActivity;

@ActivityDescription(
		outputs = {S1Step2.class,S1Step3.class},
		lane = S1Lane1.class
		)
public class S1Step1 extends ReactiveServiceActivity<S1ExamplePool> {

	@SuppressWarnings("unused")
	private String localText;
	
	@Override
	public Class<? extends Activity<S1ExamplePool>> doExecute()  throws Exception {
		
		switch( ((S1ExamplePool)getContext().getPool()).getText1() ) {
		case "error1":
			throw new TaskException("Mist", S1TheEnd.class);
		case "fatal":
			throw new Exception("Mist");
		case "second":
			return S1Step2.class;
		case "third":
			return S1Step3.class;
		}
		return null;
	}

}
