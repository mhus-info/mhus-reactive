package de.mhus.cherry.reactive.util.bpmn2;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.util.activity.REvent;

public abstract class RTimerEvent<P extends RPool<?>> extends REvent<P> {

	@Override
	public void initializeActivity() throws Exception {
		long scheduled = getScheduledTime();
		getContext().getPNode().setState(STATE_NODE.SCHEDULED);
		getContext().getPNode().setScheduled(scheduled);
	}

	/**
	 * Return next scheduled time
	 * @return
	 */
	protected abstract long getScheduledTime();

}
