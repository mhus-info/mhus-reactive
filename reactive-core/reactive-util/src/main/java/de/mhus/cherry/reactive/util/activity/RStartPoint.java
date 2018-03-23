package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AStartPoint;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;

public class RStartPoint<P extends RPool<?>> extends RActivity<P> implements AStartPoint<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		EElement eNode = getContext().getENode();
		for (Output output : eNode.getActivityDescription().outputs()) {
			try {
				getContext().createActivity(output.activity());
			} catch (Throwable t) {
				log().w(output,t);
			}
		}
		getContext().getPNode().setState(STATE_NODE.CLOSED);
	}

	
}
