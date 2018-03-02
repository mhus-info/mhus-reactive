package de.mhus.cherry.reactive.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.mhus.cherry.reactive.model.activity.Pool;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.lib.annotations.adb.DbPersistent;
import de.mhus.lib.annotations.adb.DbPrimaryKey;
import de.mhus.lib.annotations.adb.DbRelation;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.core.pojo.PojoModelFactory;
import de.mhus.lib.core.pojo.PojoParser;

public abstract class ReactivePool<P extends Pool<?>> extends MLog implements Pool<P>, PojoModelFactory, ContextRecipient {

	private PojoModel pojoModel;
	protected ProcessContext<?> context;

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
	public void initializeCase(Map<String, Object> parameters) throws Exception {
		checkInputParameters(parameters);
		importParameters(parameters);
	}

	/**
	 * Check and manipulate incoming parameters before they are written to the case.
	 * Throw an exception if the parameters are not valid.
	 * 
	 * @param parameters
	 * @throws Exception
	 */
	protected abstract void checkInputParameters(Map<String, Object> parameters) throws Exception;
	
	@Override
	public void closeCase() {
		
	}

	public synchronized PojoModel getPojoModel() {
		if (pojoModel == null)
			pojoModel = createPojoModel(this.getClass());
		return pojoModel;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public PojoModel createPojoModel(Class<?> clazz) {
		return new PojoParser().parse(clazz, "_", new Class[] { DbPersistent.class, DbPrimaryKey.class, DbRelation.class }).filter(true,false,true,false,true).getModel();
	}
	
	@Override
	public void setContext(ProcessContext<?> context) {
		this.context = context;
	}

}
