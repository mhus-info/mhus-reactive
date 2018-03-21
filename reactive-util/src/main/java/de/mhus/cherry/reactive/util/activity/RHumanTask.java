package de.mhus.cherry.reactive.util.activity;

import java.util.Map.Entry;

import de.mhus.cherry.reactive.model.activity.AHumanTask;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.lib.annotations.adb.DbPersistent;
import de.mhus.lib.annotations.pojo.Hidden;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.pojo.MPojo;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.core.pojo.PojoParser;

public abstract class RHumanTask<P extends RPool<?>> extends RAbstractTask<P> implements AHumanTask<P> {

	@Override
	public void initializeActivity() throws Exception {
		getContext().getPNode().setState(STATE_NODE.WAITING);
		getContext().getPNode().setType(TYPE_NODE.HUMAN);
	}

	@Override
	@Hidden
	public IProperties getFormValues() {
		
		P pool = getContext().getPool();
		PojoModel modelTask = createFormPojoModel(getClass());
		PojoModel modelPool = createFormPojoModel(pool.getClass());
		
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
	
	@SuppressWarnings("unchecked")
	public PojoModel createFormPojoModel(Class<?> clazz) {
		return new PojoParser().parse(clazz, ".", new Class[] { DbPersistent.class }).filter(true,false,true,true,true).getModel();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setFormValues(IProperties values) {
		P pool = getContext().getPool();
		PojoModel modelTask = createFormPojoModel(getClass());
		PojoModel modelPool = createFormPojoModel(pool.getClass());
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
