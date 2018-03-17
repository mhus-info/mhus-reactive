package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.errors.TaskException;
import de.mhus.cherry.reactive.util.activity.RServiceTask;

@ActivityDescription(
		outputs = {
				@Output(activity=S1Step2.class),
				@Output(activity=S1Step3.class)
				},
		lane = S1Lane1.class,
		triggers = {
				@Trigger(type=TYPE.DEFAULT_ERROR,activity=S1TheEnd.class),
				@Trigger(type=TYPE.ERROR,activity=S1TheEnd.class, name="error1")
		}
		)
public class S1Step1 extends RServiceTask<S1Pool> {

	@SuppressWarnings("unused")
	private String localText;
	
	@Override
	public Class<? extends AActivity<S1Pool>> doExecute()  throws Exception {
		
		switch( getPool().getText1() ) {
		case "error1":
			throw new TaskException("Mist", "error1");
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
