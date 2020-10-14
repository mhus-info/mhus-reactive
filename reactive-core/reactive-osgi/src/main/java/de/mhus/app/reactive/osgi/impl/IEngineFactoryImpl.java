/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.reactive.osgi.impl;

import java.io.File;
import java.util.Locale;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import de.mhus.app.reactive.engine.UiEngine;
import de.mhus.app.reactive.model.ui.IEngine;
import de.mhus.app.reactive.model.ui.IEngineFactory;
import de.mhus.app.reactive.osgi.IEngineAdmin;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.SoftHashMap;

@Component(service = {IEngineFactory.class, IEngineAdmin.class})
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
            if (engine != null && !engine.isClosed()) return engine;
            ReactiveAdmin api = M.l(ReactiveAdmin.class);
            engine = new UiEngine(api.getEngine(), user, locale);
            ((UiEngine) engine).setDefaultProcessProperties(defaultProcessProperties);
            cache.put(user + "#" + locale.getLanguage(), engine);
            return engine;
        }
    }

    @Override
    public void cleanupCache() {

        File f = new File("etc/de.mhus.app.reactive.model.ui.IEngineFactory.properties");
        if (f.exists()) defaultProcessProperties = MProperties.load(f);

        synchronized (cache) {
            cache.cleanup();
        }
    }
}
