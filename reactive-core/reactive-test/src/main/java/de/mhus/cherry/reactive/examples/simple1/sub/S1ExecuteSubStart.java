package de.mhus.cherry.reactive.examples.simple1.sub;

import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.SubDescription;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.cherry.reactive.util.activity.RSubStart;

@ActivityDescription(
		outputs=@Output(activity=S1TheEnd.class)
		)
@SubDescription(
		start=S1StartSpock.class
		)
public class S1ExecuteSubStart extends RSubStart<S1Pool> {

	@Override
	protected void prepareNewRuntime(RuntimeNode runtime) {
		System.out.println(">>> prepareNewRuntime");
	}

	@Override
	protected String doExecuteAfterSub(ProcessContext<?> closingContext) {
		System.out.println("<<< doExecuteAfterSub");
		return null;
	}

}
