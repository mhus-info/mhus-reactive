package de.mhus.cherry.reactive.engine.ui;

import java.util.Locale;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MProperties;

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
						properties.setString(pUri + "#index" + i, index[i]);
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
						properties.setString(eUri + "#index" + i, index[i]);
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

	@Override
	public String getIndexDisplayName(int index, String uri, String canonicalName) {
		Locale locale = engine.getLocale();
		if (locale != null) {
			String out = properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#index"+index+"?" + locale.getLanguage(), null);
			if (out != null) return out;
		}
		return properties.getString(uri + (canonicalName == null ? "" : "/" + canonicalName) + "#index"+index, "Index" + index);
	}
	
}
