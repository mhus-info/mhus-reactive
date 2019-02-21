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
package de.mhus.cherry.reactive.engine.ui;

import java.lang.reflect.InvocationTargetException;

import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.ui.IPool;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.model.util.NoForm;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.form.IFormInformation;

public class UiPool extends MLog implements IPool {

	@SuppressWarnings("unused")
	private UiEngine engine;
	private MProperties properties = new MProperties();
	private String pUri;
	private PoolDescription pd;

	public UiPool(UiEngine engine, EProcess process, EPool pool, MProperties defaultProcessProperties) {
		this.engine = engine;
		if (pool == null) return;
		
		pUri = EngineConst.SCHEME_REACTIVE + "://" + process.getCanonicalName() + ":" + process.getVersion() + "/" + pool.getCanonicalName();

		pd = pool.getPoolDescription();
		if (pd != null) { // paranoia
			properties.setString(pUri + "#displayName", pd.displayName().length() == 0 ? pool.getName() : pd.displayName());
			properties.setString(pUri + "#description", pd.description());
			String[] index = pd.indexDisplayNames();
			for (int i = 0; i < Math.min(index.length, EngineConst.MAX_INDEX_VALUES); i++) {
				if (index[i] != null)
					properties.setString(pUri + "#pnode.index" + i, index[i]);
			}
			PojoModel pojoModel = ActivityUtil.createPojoModel(pool.getPoolClass());
			for( PojoAttribute<?> attr : pojoModel) {
				String name = attr.getName();
				PropertyDescription desc = attr.getAnnotation(PropertyDescription.class);
				if (desc != null) {
					if (desc.displayName().length() != 0)
						name = desc.displayName();
					else
					if (desc.name().length() != 0)
						name = desc.name();
				}
				properties.setString(pUri + "#case." + attr.getName(), name);
			}
		}
		properties.putAll(defaultProcessProperties);
	}

	@Override
	public String getDisplayName() {
		return properties.getString(pUri + "#displayName", null);
	}

	@Override
	public String getDescription() {
		return properties.getString(pUri + "#description", null);
	}

	@Override
	public IFormInformation getInitialForm() {
		Class<? extends IFormInformation> form = pd.initialForm();
		if (form == null || form.getCanonicalName().equals(NoForm.class.getCanonicalName())) return null;
		try {
			return form.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			log().e(e);
		}
		return null;
	}

	@Override
	public IFormInformation getDisplayForm() {
		Class<? extends IFormInformation> form = pd.displayForm();
		if (form == null || form.getCanonicalName().equals(NoForm.class.getCanonicalName())) return null;
		try {
			return form.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			log().e(e);
		}
		return null;
	}

}
