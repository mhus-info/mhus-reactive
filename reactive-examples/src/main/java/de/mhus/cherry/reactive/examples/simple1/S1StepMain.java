package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.examples.simple1.events.S1EventExternal;
import de.mhus.cherry.reactive.examples.simple1.events.S1EventMessage;
import de.mhus.cherry.reactive.examples.simple1.events.S1EventSignal;
import de.mhus.cherry.reactive.examples.simple1.exclusive.S1GatewayExclusive;
import de.mhus.cherry.reactive.examples.simple1.parallel.S1GatewayParallel1;
import de.mhus.cherry.reactive.examples.simple1.parallel.S1GatewayParallel2;
import de.mhus.cherry.reactive.examples.simple1.trigger.S1StepTrigger;
import de.mhus.cherry.reactive.examples.simple1.trigger.S1StepTriggerTimer;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.errors.TaskException;
import de.mhus.cherry.reactive.util.activity.RServiceTask;
import de.mhus.cherry.reactive.util.activity.RTask;

@ActivityDescription(
		outputs = {
				@Output(name="step2",activity=S1Step2.class),
				@Output(name="step3",activity=S1Step3.class),
				@Output(name="human",activity=S1HumanStep.class),
				@Output(name="external",activity=S1EventExternal.class),
				@Output(name="message",activity=S1EventMessage.class),
				@Output(name="signal",activity=S1EventSignal.class),
				@Output(name="exclusive",activity=S1GatewayExclusive.class),
				@Output(name="trigger",activity=S1StepTrigger.class),
				@Output(name="triggertimer",activity=S1StepTriggerTimer.class),
				@Output(name="parallel1",activity=S1GatewayParallel1.class),
				@Output(name="parallel2",activity=S1GatewayParallel2.class)
				},
		lane = S1Lane1.class,
		triggers = {
				@Trigger(type=TYPE.DEFAULT_ERROR,activity=S1TheEnd.class),
				@Trigger(type=TYPE.ERROR,activity=S1TheEnd.class, name="error1")
		}
		)
public class S1StepMain extends RServiceTask<S1Pool> {

	@SuppressWarnings("unused")
	private String localText;
	
	@Override
	public String doExecute()  throws Exception {
		
		switch( getPool().getText1() ) {
		case "error1":
			throw new TaskException("Mist", "error1");
		case "fatal":
			throw new Exception("Mist");
		case "second":
			return "step2";
		case "third":
			return "step3";
		case "Moin":
			return RETRY;
		default:
			return getPool().getText1();
		}
	}

}
