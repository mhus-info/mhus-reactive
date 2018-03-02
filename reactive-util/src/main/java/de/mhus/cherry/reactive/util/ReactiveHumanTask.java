package de.mhus.cherry.reactive.util;

import java.io.IOException;

import de.mhus.cherry.reactive.model.activity.HumanTask;
import de.mhus.cherry.reactive.model.activity.Pool;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.pojo.MPojo;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.form.DataSource;
import de.mhus.lib.form.UiComponent;

public abstract class ReactiveHumanTask<P extends Pool> implements HumanTask<P> {

	@Override
	public ProcessContext<P> getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSource createDataSource() {
		return new TaskDataSource();
	}
	
	protected class TaskDataSource extends MLog implements DataSource {

		private PojoModel modelTask;
		private PojoModel modelPool;

		public TaskDataSource() {
			modelTask = MPojo.getDefaultModelFactory().createPojoModel(ReactiveHumanTask.this.getClass());
			modelPool = MPojo.getDefaultModelFactory().createPojoModel(ReactiveHumanTask.this.getContext().getPool().getClass());
		}

		@Override
		public boolean getBoolean(UiComponent component, String name, boolean def) {
			try {
				log().t("getBoolean",component,name,def);
				PojoAttribute<?> attr = modelTask.getAttribute(getName(component,name));
				Object pojo = ReactiveHumanTask.this;
				if (attr == null) {
					attr = modelPool.getAttribute(getName(component,name));
					pojo = ReactiveHumanTask.this.getContext().getPool();
				}
				return (Boolean)attr.get(pojo);
			} catch (Throwable e) {
				log().t(e);
			}
			return def;
		}

		protected String getName(UiComponent component, String name) {
			String ret = (component.getName() + name).toLowerCase();
			return ret;
		}

		@Override
		public int getInt(UiComponent component, String name, int def) {
			try {
				log().t("getInt",component,name,def);
				PojoAttribute<?> attr = modelTask.getAttribute(getName(component,name));
				Object pojo = ReactiveHumanTask.this;
				if (attr == null) {
					attr = modelPool.getAttribute(getName(component,name));
					pojo = ReactiveHumanTask.this.getContext().getPool();
				}
				return (Integer)attr.get(pojo);
			} catch (Throwable e) {
				log().t(e);
			}
			return def;
		}

		@Override
		public String getString(UiComponent component, String name, String def) {
			try {
				log().t("getString",component,name,def);
				PojoAttribute<?> attr = modelTask.getAttribute(getName(component,name));
				Object pojo = ReactiveHumanTask.this;
				if (attr == null) {
					attr = modelPool.getAttribute(getName(component,name));
					pojo = ReactiveHumanTask.this.getContext().getPool();
				}
				String ret = (String) attr.get(pojo);
				if (ret == null) return def;
				return ret;
			} catch (Throwable e) {
				log().t(e);
			}
			return def;
		}

		@Override
		public Object getObject(UiComponent component, String name, Object def) {
			try {
				log().t("getObject",component,name,def);
				PojoAttribute<?> attr = modelTask.getAttribute(getName(component,name));
				Object pojo = ReactiveHumanTask.this;
				if (attr == null) {
					attr = modelPool.getAttribute(getName(component,name));
					pojo = ReactiveHumanTask.this.getContext().getPool();
				}
				Object ret = attr.get(pojo);
				if (ret == null) return def;
				return ret;
			} catch (Throwable e) {
				log().t(e);
			}
			return def;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void setObject(UiComponent component, String name, Object value) throws IOException {
			log().t("setObject",component,name,value);
			@SuppressWarnings("rawtypes")
			PojoAttribute attr = modelTask.getAttribute(getName(component,name));
			Object pojo = ReactiveHumanTask.this;
			if (attr == null) {
				attr = modelPool.getAttribute(getName(component,name));
				pojo = ReactiveHumanTask.this.getContext().getPool();
			}
			attr.set(pojo, value);
		}

		@Override
		public DataSource getNext() {
			return null;
		}
		
	}
}
