package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AHumanTask;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.lib.annotations.pojo.Hidden;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.definition.IDefDefinition;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.definition.FmElement;

public abstract class RHumanTask<P extends RPool<?>> extends RAbstractTask<P> implements AHumanTask<P> {

	@Override
	public void initializeActivity() throws Exception {
		getContext().getPNode().setState(STATE_NODE.WAITING);
		getContext().getPNode().setType(TYPE_NODE.HUMAN);
	}

	@Override
	public String doExecute() {
		return null;
	}

	@Override
	@Hidden
	public IProperties getFormValues() throws MException {
		
		DefRoot form = createForm().build().getRoot();
		
		P pool = getContext().getPool();
		PojoModel modelTask = ActivityUtil.createFormPojoModel(getClass());
		PojoModel modelPool = ActivityUtil.createFormPojoModel(pool.getClass());
		MProperties out = new MProperties();

		// return only in the form defined values
		for (IDefDefinition item : form.definitions()) {
			if (item instanceof FmElement) {
				FmElement ele = (FmElement)item;
				String name = ele.getProperty("name");
				String namePrefix = name + ".";
				// first pool
				for (PojoAttribute<?> attr : modelPool) {
					String aName = attr.getName();
					if (aName.equals(name) || aName.startsWith(namePrefix)) {
						try {
							out.put(attr.getName(), attr.get(pool));
							if (aName.equals(name)) {
								PropertyDescription desc = attr.getAnnotation(PropertyDescription.class);
								if (!desc.writable()) {
									out.put(name + ".editable", false);
								}
							}
						} catch (Throwable t) {
							log().w(this,attr,t);
						}
					}
				}
				//overwrite with task
				for (PojoAttribute<?> attr : modelTask) {
					String aName = attr.getName();
					if (aName.equals(name) || aName.startsWith(namePrefix)) {
						try {
							out.put(attr.getName(), attr.get(this));
							if (aName.equals(name)) {
								PropertyDescription desc = attr.getAnnotation(PropertyDescription.class);
								if (!desc.writable()) {
									out.put(name + ".editable", false);
								}
							}
						} catch (Throwable t) {
							log().w(this,attr,t);
						}
					}
				}

			}
		}
		
		return out;
	}
	
	@Override
	@SuppressWarnings({ "unchecked"})
	public void doSubmit(IProperties values) throws MException {
		P pool = getContext().getPool();
		PojoModel modelTask = ActivityUtil.createFormPojoModel(getClass());
		PojoModel modelPool = ActivityUtil.createFormPojoModel(pool.getClass());
		
		DefRoot form = createForm().build().getRoot();

		for (IDefDefinition item : form.definitions()) {
			if (item instanceof FmElement) {
				FmElement ele = (FmElement)item;
				String name = ele.getProperty("name");
				Object value = values.get(name);
				if (value != null) {
					if (modelTask.hasAttribute(name)) {
						PojoAttribute<Object> attr = modelTask.getAttribute(name);
						PropertyDescription desc = attr.getAnnotation(PropertyDescription.class);
						if (desc.writable()) {
							try {
								attr.set(this, value);
							} catch (Throwable t) {
								log().w(this,attr,t);
							}
						}
					} else
					if (modelPool.hasAttribute(name)) {
						PojoAttribute<Object> attr = modelPool.getAttribute(name);
						PropertyDescription desc = attr.getAnnotation(PropertyDescription.class);
						if (desc.writable()) {
							try {
								attr.set(pool, value);
							} catch (Throwable t) {
								log().w(this,attr,t);
							}
						}
					}
				}
			}
		}
		
		doSubmit();
		
	}

	protected abstract void doSubmit() throws MException;
	
}
