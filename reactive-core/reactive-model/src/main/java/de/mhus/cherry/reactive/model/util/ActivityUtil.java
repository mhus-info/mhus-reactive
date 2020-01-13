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
package de.mhus.cherry.reactive.model.util;

import java.util.LinkedList;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.core.pojo.PojoParser;

public class ActivityUtil {

	private static Log log = Log.getLog(ActivityUtil.class);
	
	public static Class<? extends AActivity<?>> getOutputByName(AActivity<?> element, String name) {
		return getOutputByName(element.getClass(), name);
	}
	
	@SuppressWarnings("rawtypes")
	public static Class<? extends AActivity<?>> getOutputByName(Class<? extends AActivity> element, String name) {
		ActivityDescription desc = element.getAnnotation(ActivityDescription.class);
		if (desc == null) return null;
		for (Output output : desc.outputs())
			if (name.equals(output.name()))
				return output.activity();
		return null;
	}

	public static String getEvent(AActivity<?> element) {
		return getEvent(element.getClass());
	}

	@SuppressWarnings("rawtypes")
	public static String getEvent(Class<? extends AActivity> element) {
		ActivityDescription desc = element.getAnnotation(ActivityDescription.class);
		if (desc == null || desc.event().length() == 0) return null;
		return desc.event();
	}

	public static Trigger[] getTriggers(AActivity<?> element) {
		return getTriggers(element.getClass());
	}
	
	@SuppressWarnings("rawtypes")
	public static Trigger[] getTriggers(Class<? extends AActivity> element) {
		ActivityDescription desc = element.getAnnotation(ActivityDescription.class);
		if (desc == null || desc.triggers() == null) return new Trigger[0];
		return desc.triggers();
	}

	public static Output[] getOutputs(AActivity<?> element) {
		return getOutputs(element.getClass());
	}
	
	@SuppressWarnings("rawtypes")
	public static Output[] getOutputs(Class<? extends AActivity> element) {
		ActivityDescription desc = element.getAnnotation(ActivityDescription.class);
		if (desc == null || desc.outputs() == null) return new Output[0];
		return desc.outputs();
	}
	
	public static EElement[] getInputs(AActivity<?> element) {
		return getInputs(element.getContext(), element.getClass());
	}
	
	public static EElement[] getInputs(ProcessContext<?> context, @SuppressWarnings("rawtypes") Class<? extends AActivity> element) {
		LinkedList<EElement> out = new LinkedList<>();
		
		EPool pool = context.getEPool();
		for (String name : pool.getElementNames()) {
			EElement item = pool.getElement(name);
			ActivityDescription desc = item.getActivityDescription();
			if (desc != null) {
				for (Output output : desc.outputs()) {
					if (output.activity() == element)
						out.add(item);
				}
				for (Trigger trigger : desc.triggers()) {
					if (trigger.activity() == element)
						out.add(item);
				}
			}
		}
		
		return (EElement[]) out.toArray(new EElement[out.size()]);
	}

	@SuppressWarnings("unchecked")
	public static PojoModel createPojoModel(Class<?> clazz) {
		return new PojoParser().parse(clazz, "_", new Class[] { PropertyDescription.class }).filter(true,false,true,true,true).getModel();
	}

	@SuppressWarnings("unchecked")
	public static PojoModel createFormPojoModel(Class<?> clazz) {
		return new PojoParser().parse(clazz, "_", new Class[] { PropertyDescription.class }).filter(true,false,true,true,true).getModel();
	}
	
	/**
	 * Inspect pool and activity to get the values of the fields into the
	 * returned properties object. The activity fields have higher priority.
	 * 
	 * @param pool The pool or null
	 * @param activity The activity or null
	 * @return The values
	 */
	public static IProperties prepareProperties(APool<?> pool, AActivity<?> activity) {
		MProperties out = new MProperties();
		if (activity != null) {
			PojoModel model = createPojoModel(activity.getClass());
			for (PojoAttribute<?> attr : model) {
				try {
					out.put(attr.getName(), attr.get(activity));
				} catch (Throwable t) {
					log.t(attr,t);
				}
			}
		}
		if (pool != null) {
			PojoModel model = createPojoModel(pool.getClass());
			for (PojoAttribute<?> attr : model) {
				if (!out.containsKey(attr.getName()))
					try {
						out.put(attr.getName(), attr.get(pool));
					} catch (Throwable t) {
						log.t(attr,t);
					}
			}
		}
		return out;
	}

}
