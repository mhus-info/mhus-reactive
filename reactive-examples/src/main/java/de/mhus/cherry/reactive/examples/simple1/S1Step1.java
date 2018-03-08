package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.errors.TaskException;
import de.mhus.cherry.reactive.util.ReactiveServiceTask;

@ActivityDescription(
		outputs = {S1Step2.class,S1Step3.class},
		lane = S1Lane1.class,
		triggers = {
				@Trigger(type=TYPE.DEFAULT_ERROR,activity=S1TheEnd.class)
		}
		)
public class S1Step1 extends ReactiveServiceTask<S1Pool> {

	@SuppressWarnings("unused")
	private String localText;
	
	@Override
	public Class<? extends Activity<S1Pool>> doExecute()  throws Exception {
		
		switch( ((S1Pool)getContext().getPool()).getText1() ) {
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
