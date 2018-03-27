package de.mhus.cherry.reactive.engine.ui;

import de.mhus.cherry.reactive.model.ui.ICaseDescription;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

public class UiCaseDescription extends MLog implements ICaseDescription {

	private IProcess process;
	private String uri;

	public UiCaseDescription(UiEngine ui, String uri) {
		this.uri = uri;
		try {
			process = ui.getProcess(uri);
		} catch (MException e) {
			log().d(uri,e);
		}
	}

	@Override
	public String getDisplayName() {
		return process.getDisplayName(uri, null);
	}

	@Override
	public String getDescription() {
		return process.getDescription(uri, null);
	}

	@Override
	public String getPropertyName(String property) {
		return process.getPropertyName(uri, null, property);
	}

}
