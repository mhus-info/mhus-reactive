package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.ACondition;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class S1ConditionSpock implements ACondition<S1Pool> {

	@Override
	public int check(ProcessContext<S1Pool> context) {
		S1Pool pool = context.getPool();
		return pool.getText2().equals("spock") ? TRUE : FALSE;
	}

}
