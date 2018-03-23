package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AGateway;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;

public abstract class RGateway<P extends RPool<?>> extends RActivity<P> implements AGateway<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		Output[] next = doExecute();
		if (next != null) {
			for (Output output : next) {
				Class<? extends AActivity<?>> act = output.activity();
				try {
					getContext().createActivity(act);
				} catch (Throwable t) {
					log().w(act,t);
				}
			}
			getContext().getPNode().setState(STATE_NODE.CLOSED);
		}
	}

	public abstract Output[] doExecute()  throws Exception;

}
