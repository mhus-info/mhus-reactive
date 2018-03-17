package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AEndPoint;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;

public class REndPoint<P extends RPool<?>> extends RActivity<P> implements AEndPoint<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		// only if the last one with this runtime
//		getContext().getPRuntime().setState(STATE.CLOSED);
//		getContext().saveRuntime();
		getContext().getPNode().setState(STATE_NODE.CLOSED);
	}

}
