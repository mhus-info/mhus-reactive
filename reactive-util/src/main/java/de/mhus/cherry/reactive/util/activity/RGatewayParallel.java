package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AExclusiveGateway;
import de.mhus.cherry.reactive.model.activity.AParallelGateway;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.util.ActivityUtil;

/**
 * Execute all Output(s) parallel.
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public class RGatewayParallel<P extends RPool<?>> extends RGateway<P> implements AParallelGateway<P> {

	@Override
	public Output[] doExecute() throws Exception {
		return ActivityUtil.getOutputs(this);
	}

}
