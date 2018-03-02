package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class NoActivity implements Activity<NoPool> {

	@Override
	public ProcessContext<NoPool> getContext() {
		return null;
	}

}
