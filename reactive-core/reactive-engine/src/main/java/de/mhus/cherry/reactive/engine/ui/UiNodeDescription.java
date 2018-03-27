package de.mhus.cherry.reactive.engine.ui;

import de.mhus.cherry.reactive.model.ui.INodeDescription;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

public class UiNodeDescription extends MLog implements INodeDescription {

	private String uri;
	private String name;
	private IProcess process;

	public UiNodeDescription(UiEngine ui, String uri, String name) {
		this.uri = uri;
		this.name = name;
		try {
			process = ui.getProcess(uri);
		} catch (MException e) {
			log().d(uri,e);
		}
	}

	@Override
	public String getDisplayName() {
		return process.getDisplayName(uri, name);
	}

	@Override
	public String getDescription() {
		return process.getDescription(uri, name);
	}

	@Override
	public String getPropertyName(String property) {
		return process.getPropertyName(uri, name, property);
	}

}
