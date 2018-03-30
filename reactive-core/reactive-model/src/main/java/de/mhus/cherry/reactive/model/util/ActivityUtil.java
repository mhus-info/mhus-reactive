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
/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.mhus.cherry.reactive.model.util;

import java.util.LinkedList;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.lib.annotations.generic.Public;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.core.pojo.PojoParser;

public class ActivityUtil {

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
	
	public static Class<? extends AElement<?>>[] getInputs(AActivity<?> element) {
		return getInputs(element.getContext(), element.getClass());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Class<? extends AElement<?>>[] getInputs(ProcessContext<?> context, Class<? extends AActivity> element) {
		LinkedList<Class<? extends AElement<?>>> out = new LinkedList<>();
		
		EPool pool = context.getEPool();
		for (String name : pool.getElementNames()) {
			EElement item = pool.getElement(name);
			ActivityDescription desc = item.getActivityDescription();
			if (desc != null) {
				for (Output output : desc.outputs()) {
					if (output.activity() == element)
						out.add(item.getElementClass());
				}
				for (Trigger trigger : desc.triggers()) {
					if (trigger.activity() == element)
						out.add(item.getElementClass());
				}
			}
		}
		
		return (Class<? extends AActivity<?>>[]) out.toArray(new Class<?>[out.size()]);
	}

	@SuppressWarnings("unchecked")
	public static PojoModel createPojoModel(Class<?> clazz) {
		return new PojoParser().parse(clazz, "_", new Class[] { PropertyDescription.class }).filter(true,false,true,true,true).getModel();
	}

	@SuppressWarnings("unchecked")
	public static PojoModel createFormPojoModel(Class<?> clazz) {
		return new PojoParser().parse(clazz, "_", new Class[] { PropertyDescription.class }).filter(true,false,true,true,true).getModel();
	}

}
