/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.util.bpmn2;

import de.mhus.cherry.reactive.model.activity.AUserTask;
import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.util.activity.RAbstractTask;
import de.mhus.lib.annotations.pojo.Hidden;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.definition.IDefDefinition;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.definition.FmElement;

public abstract class RUserTask<P extends RPool<?>> extends RAbstractTask<P> implements AUserTask<P> {

	@Override
	public void initializeActivity() throws Exception {
		getContext().getPNode().setState(STATE_NODE.WAITING);
		getContext().getPNode().setType(TYPE_NODE.USER);
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