package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.ACondition;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class NoCondition implements ACondition<APool<?>> {

	@Override
	public int check(ProcessContext<APool<?>> context) {
		return Integer.MIN_VALUE;
	}

}
