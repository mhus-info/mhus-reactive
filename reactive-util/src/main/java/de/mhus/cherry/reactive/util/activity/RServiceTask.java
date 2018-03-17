package de.mhus.cherry.reactive.util.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.mhus.cherry.reactive.model.activity.AServiceTask;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;

public abstract class RServiceTask<P extends RPool<?>> extends RTask<P> implements AServiceTask<P> {

	private PojoModel pojoModel;

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

	@Override
	public synchronized PojoModel getPojoModel() {
		if (pojoModel == null)
			pojoModel = getContext().getPool().createPojoModel(this.getClass());
		return pojoModel;
	}

}
