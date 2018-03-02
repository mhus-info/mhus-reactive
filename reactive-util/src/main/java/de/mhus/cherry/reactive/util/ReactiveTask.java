package de.mhus.cherry.reactive.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.mhus.cherry.reactive.model.activity.Task;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;

public abstract class ReactiveTask<P extends ReactivePool<?>> extends MLog implements Task<P>, ContextRecipient {

	private PojoModel pojoModel;
	private ProcessContext<P> context;

	@Override
	public ProcessContext<P> getContext() {
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContext(ProcessContext<?> context) {
		this.context = (ProcessContext<P>) context;
	}

	@Override
	public void initializeTask() {
		
	}
	
	@Override
	public Map<String, Object> exportParamters() {
		HashMap<String,Object> out = new HashMap<>();
		for( PojoAttribute<?> attr : getPojoModel()) {
			try {
				Object value = attr.get(this);
				if (value != null)
					out.put(attr.getName(), value);
			} catch (IOException e) {
				log().d(attr,e);
			}
		}
		return out;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void importParameters(Map<String, Object> parameters) {
		for(PojoAttribute attr : getPojoModel()) {
			try {
				Object value = parameters.get(attr.getName());
				if (value != null)
					attr.set(this, value);
			} catch (IOException e) {
				log().d(attr,e);
			}
		}
	}

	public synchronized PojoModel getPojoModel() {
		if (pojoModel == null)
			pojoModel = getContext().getPool().createPojoModel(this.getClass());
		return pojoModel;
	}

}
