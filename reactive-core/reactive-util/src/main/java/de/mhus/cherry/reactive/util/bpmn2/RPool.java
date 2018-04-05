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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;

public abstract class RPool<P extends APool<?>> extends MLog implements APool<P>, ContextRecipient {

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
			pojoModel = ActivityUtil.createPojoModel(this.getClass());
		return pojoModel;
	}
		
	@Override
	public void setContext(ProcessContext<?> context) {
		this.context = context;
	}

}
