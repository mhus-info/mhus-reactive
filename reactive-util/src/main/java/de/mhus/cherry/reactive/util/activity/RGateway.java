package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AGateway;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;

public abstract class RGateway<P extends RPool<?>> extends RActivity<P> implements AGateway<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		Class<? extends AActivity<?>>[] next = doExecute();
		if (next != null) {
			for (Class<? extends AActivity<?>> n : next) {
				try {
					getContext().createActivity(n);
				} catch (Throwable t) {
					log().w(n,t);
				}
			}
			getContext().getPNode().setState(STATE_NODE.CLOSED);
		}
	}

	public abstract Class<? extends AActivity<?>>[] doExecute()  throws Exception;

}
