package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AEndPoint;

public abstract class RTerminate<P extends RPool<?>> extends RActivity<P> implements AEndPoint<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		getContext().getPCase().close(getExitCode(), getExitMessage());
	}

	protected abstract int getExitCode();

	protected abstract String getExitMessage();

}
