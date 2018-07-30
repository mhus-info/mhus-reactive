package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AServiceTask;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.util.bpmn2.RPool;
import de.mhus.lib.core.MTimeInterval;

/**
 * Use this task type to check something in intervals. The default interval is five minutes.
 * You can change the interval using the ActivityDescription.event() parameter.
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public abstract class RRetryTask<P extends RPool<?>> extends RTask<P> implements AServiceTask<P> {

	protected String output = null;

	@Override
	public String doExecute() throws Exception {
		
		boolean done = doRetry();
		
		if (!done) {
			String interval = getClass().getAnnotation(ActivityDescription.class).event();
			long newSchedule = System.currentTimeMillis() + MTimeInterval.toMilliseconds(interval, 60000 * 5);
			getContext().getPNode().setScheduled(newSchedule);
			getContext().getPNode().setTryCount(3);
			getContext().getPNode().setState(STATE_NODE.SCHEDULED);
			return RETRY;
		} else {
			getContext().getPNode().setState(STATE_NODE.CLOSED);
		}
		
		return output;
	}
	
	protected abstract boolean doRetry() throws Exception;

}
