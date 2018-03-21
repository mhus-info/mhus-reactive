package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AEvent;
import de.mhus.cherry.reactive.model.activity.ATask;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.errors.EngineException;
import de.mhus.cherry.reactive.model.util.ActivityUtil;

public abstract class RAbstractEvent<P extends RPool<?>> extends RActivity<P> implements AEvent<P> {

	protected String outputName = DEFAULT_OUTPUT;

	@Override
	public void doExecuteActivity() throws Exception {
		doExecute();
		if (outputName == null) outputName = DEFAULT_OUTPUT;
		if (!outputName.equals(RETRY)) {
			Class<? extends AActivity<?>> next = ActivityUtil.getOutputByName(this, outputName);
			if (next == null)
				throw new EngineException("Output Activity not found: " + outputName + " in " + getClass().getCanonicalName());
			getContext().createActivity(next);
			getContext().getPNode().setState(STATE_NODE.CLOSED);
		}
	}

	public abstract void doExecute()  throws Exception;

}
