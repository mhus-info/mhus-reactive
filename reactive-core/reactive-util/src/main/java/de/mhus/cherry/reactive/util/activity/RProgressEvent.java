package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.util.bpmn2.RPool;

public class RProgressEvent<P extends RPool<?>> extends RMilestoneEvent<P> {

	@Override
	protected String getMilestone() {
		return EngineConst.MILESTONE_PROGRESS;
	}

}
