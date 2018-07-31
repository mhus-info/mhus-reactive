package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AServiceTask;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.util.bpmn2.RPool;
import de.mhus.lib.core.MTimeInterval;

/**
 * Use this task type to check something in intervals. The default interval is fifteen minutes.
 * You can change the interval using the ActivityDescription.event() parameter.
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public abstract class RRetryTask<P extends RPool<?>> extends RTask<P> implements AServiceTask<P> {

	/**
	 * Overwrite to follow another branch then default.
	 */
	protected String output = null;
	/**
	 * Overwrite to change the default interval time.
	 */
	protected long interval = 0;

	public RRetryTask() {
		interval = getDefaultInterval();
	}

	/**
	 * Returns the default interval set by definition or 15 minutes as fall back.
	 * 
	 * @return Default configured interval
	 */
	public long getDefaultInterval() {
		String intervalStr = getClass().getAnnotation(ActivityDescription.class).event();
		return MTimeInterval.toMilliseconds(intervalStr, 60000 * 15);
		
	}
	
	@Override
	public String doExecute() throws Exception {
		
		boolean done = doRetry();
		
		if (!done) {
			long newSchedule = System.currentTimeMillis() + interval;
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
