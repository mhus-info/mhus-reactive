package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.util.bpmn2.RPool;

public abstract class RMilestoneEvent<P extends RPool<?>> extends REvent<P> {

	@Override
	public void doExecute() throws Exception {
		getContext().getPCase().setMilestone(getMilestone());
	}

	/**
	 * Return the new milestone.
	 * 
	 * @return The milestone
	 */
	protected abstract String getMilestone();

}
