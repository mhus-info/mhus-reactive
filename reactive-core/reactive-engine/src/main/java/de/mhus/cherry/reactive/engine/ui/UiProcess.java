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

import java.util.Locale;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;

public class UiProcess implements IProcess {

	private MProperties properties = new MProperties();
	private UiEngine engine;
	
	public UiProcess(UiEngine engine, EProcess process) {
		this.engine = engine;
		for (String poolName : process.getPoolNames()) {
			EPool pool = process.getPool(poolName);
			String pUri = EngineConst.SCHEME_REACTIVE + "://" + process.getCanonicalName() + ":" + process.getVersion() + "/" + pool.getCanonicalName();
			
			PoolDescription pd = pool.getPoolDescription();
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
				
			
			for (String eleName : pool.getElementNames()) {
				EElement ele = pool.getElement(eleName);
				ActivityDescription desc = ele.getActivityDescription();
				if (desc == null) continue;
				String eUri = pUri + "/" + ele.getCanonicalName();
				
				properties.setString(eUri + "#displayName", desc.displayName().length() == 0 ? ele.getName() : desc.displayName());
				properties.setString(eUri + "#description", desc.description());
				String[] index = desc.indexDisplayNames();
				for (int i = 0; i < Math.min(index.length, EngineConst.MAX_INDEX_VALUES); i++) {
					if (index[i] != null)
						properties.setString(eUri + "#pnode.index" + i, index[i]);
				}
				PojoModel pojoModel = ActivityUtil.createPojoModel(ele.getElementClass());
				for( PojoAttribute<?> attr : pojoModel) {
					String name = attr.getName();
					PropertyDescription pdesc = attr.getAnnotation(PropertyDescription.class);
					if (pdesc != null) {
						if (pdesc.displayName().length() != 0)
							name = pdesc.displayName();
						else
						if (pdesc.name().length() != 0)
							name = pdesc.name();
					}
					properties.setString(pUri + "#node." + attr.getName(), name);
				}
				
			}
		}
	}

	@Override
	public String getDisplayName(String uri, String canonicalName) {
		Locale locale = engine.getLocale();
		if (locale != null) {
			String out = properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#displayName?" + locale.getLanguage(), null);
			if (out != null) return out;
		}
		return properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#displayName", canonicalName);
	}

	@Override
	public String getDescription(String uri, String canonicalName) {
		Locale locale = engine.getLocale();
		if (locale != null) {
			String out = properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#description?" + locale.getLanguage(), null);
			if (out != null) return out;
		}
		return properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#description", "");
	}

	public MProperties getProperties() {
		return properties;
	}

//	@Override
//	public String getIndexDisplayName(int index, String uri, String canonicalName) {
//		Locale locale = engine.getLocale();
//		if (locale != null) {
//			String out = properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#index"+index+"?" + locale.getLanguage(), null);
//			if (out != null) return out;
//		}
//		return properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#index"+index, "Index" + index);
//	}

	@Override
	public String getPropertyName(String uri, String canonicalName, String property) {
		if (property.startsWith(EngineConst.UI_CASE_PREFIX))
			canonicalName = null;
		Locale locale = engine.getLocale();
		if (locale != null) {
			String out = properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#"+property+"?" + locale.getLanguage(), null);
			if (out != null) return out;
		}
		return properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#"+property, property);
	}
	
}
