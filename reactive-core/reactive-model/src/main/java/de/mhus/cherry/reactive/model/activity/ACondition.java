package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.engine.ProcessContext;

public interface ACondition<P extends APool<?>> extends AElement<P> {
	
	static final int TRUE = 1;
	static final int FALSE = -1;
	
	int check(ProcessContext<P> context);
	
}
