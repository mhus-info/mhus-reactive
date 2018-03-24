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
