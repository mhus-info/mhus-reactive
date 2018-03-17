package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.ATask;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;

public abstract class RTask<P extends RPool<?>> extends RActivity<P> implements ATask<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		Class<? extends AActivity<?>> next = doExecute();
		if (next != null) {
			getContext().createActivity(next);
			getContext().getPNode().setState(STATE_NODE.CLOSED);
		}
	}

	public abstract Class<? extends AActivity<?>> doExecute()  throws Exception;

}
