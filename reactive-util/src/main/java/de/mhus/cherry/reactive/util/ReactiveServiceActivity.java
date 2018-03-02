package de.mhus.cherry.reactive.util;

import de.mhus.cherry.reactive.model.activity.Pool;
import de.mhus.cherry.reactive.model.activity.ServiceActivity;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public abstract class ReactiveServiceActivity<P extends Pool> implements ServiceActivity<P> {

	@Override
	public ProcessContext<P> getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
