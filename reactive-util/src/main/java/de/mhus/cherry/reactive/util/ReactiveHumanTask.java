package de.mhus.cherry.reactive.util;

import java.util.Map.Entry;

import de.mhus.cherry.reactive.model.activity.HumanTask;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.pojo.MPojo;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;

public abstract class ReactiveHumanTask<P extends ReactivePool<?>> extends ReactiveTask<P> implements HumanTask<P> {

	@Override
	public IProperties getFormValues() {
		
		P pool = getContext().getPool();
		PojoModel modelTask = MPojo.getDefaultModelFactory().createPojoModel(getClass());
		PojoModel modelPool = MPojo.getDefaultModelFactory().createPojoModel(pool.getClass());
		
		MProperties out = new MProperties();
		for (PojoAttribute<?> attr : modelTask)
			try {
				out.put(attr.getName(), attr.get(this));
			} catch (Throwable t) {
				log().w(this,attr,t);
			}
		for (PojoAttribute<?> attr : modelPool)
			try {
				if (!out.isProperty(attr.getName()))
					out.put(attr.getName(), attr.get(pool));
			} catch (Throwable t) {
				log().w(this,attr,t);
			}
		return out;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setFormValues(IProperties values) {
		P pool = getContext().getPool();
		PojoModel modelTask = MPojo.getDefaultModelFactory().createPojoModel(getClass());
		PojoModel modelPool = MPojo.getDefaultModelFactory().createPojoModel(pool.getClass());
		for (Entry<String, Object> entry : values.entrySet()) {
			PojoAttribute attr = modelTask.getAttribute(entry.getKey());
			Object target = this;
			if (attr == null) {
				attr = modelPool.getAttribute(entry.getKey());
				target = pool;
			}
			if (attr != null) {
				try {
					attr.set(target, entry.getValue());
				} catch (Throwable t) {
					log().w(this,attr,t);
				}
			}
		}
	}
	
}
