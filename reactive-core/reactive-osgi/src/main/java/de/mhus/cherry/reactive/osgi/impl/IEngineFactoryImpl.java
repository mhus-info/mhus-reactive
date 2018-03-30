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
package de.mhus.cherry.reactive.osgi.impl;

import java.io.File;
import java.util.Locale;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.reactive.engine.ui.UiEngine;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.osgi.IEngineAdmin;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.SoftHashMap;

@Component(provide={IEngineFactory.class,IEngineAdmin.class})
public class IEngineFactoryImpl implements IEngineFactory, IEngineAdmin {

	private SoftHashMap<String, IEngine> cache = new SoftHashMap<>();
	private MProperties defaultProcessProperties = new MProperties();
	
	@Activate
	public void doActivate(ComponentContext ctx) {
		cleanupCache();
	}

	@Override
	public IEngine create(String user, Locale locale) {
		if (locale == null) locale = Locale.getDefault();
		synchronized (cache) {
			IEngine engine = cache.get(user + "#" + locale.getLanguage());
			if (engine != null) return engine;
			ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
			engine = new UiEngine(api.getEngine(), user, locale);
			((UiEngine)engine).setDefaultProcessProperties(defaultProcessProperties);
			cache.put(user + "#" + locale.getLanguage(), engine);
			return engine;
		}
	}

	@Override
	public void cleanupCache() {
		
		File f = new File("etc/de.mhus.cherry.reactive.model.ui.IEngineFactory.properties");
		if (f.exists())
			defaultProcessProperties = MProperties.load(f);
		
		synchronized (cache) {
			cache.cleanup();
		}
	}
	
}
