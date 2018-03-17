package de.mhus.cherry.reactive.model.util;

import java.util.Map;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.lib.errors.NotSupportedException;

public class NoActivity implements AActivity<NoPool> {

	@Override
	public void initializeActivity() throws Exception {
		throw new NotSupportedException();
	}

	@Override
	public void doExecuteActivity() throws Exception {
		throw new NotSupportedException();
	}

	@Override
	public Map<String, Object> exportParamters() {
		throw new NotSupportedException();
	}

	@Override
	public void importParameters(Map<String, Object> parameters) {
		throw new NotSupportedException();
	}

	@Override
	public ProcessContext<NoPool> getContext() {
		throw new NotSupportedException();
	}

}
