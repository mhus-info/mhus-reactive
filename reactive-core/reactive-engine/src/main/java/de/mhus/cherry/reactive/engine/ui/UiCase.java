package de.mhus.cherry.reactive.engine.ui;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

public class UiCase extends MLog implements ICase {

	private PCaseInfo info;
	private IEngine ui;
	private IProcess process;

	public UiCase(IEngine ui, PCaseInfo info) {
		this.ui = ui;
		this.info = info;
//		this.uri = info.getUri();
//		this.caseCanonicalName = info.getCanonicalName();
//		this.indexValues = new String[EngineConst.MAX_INDEX_VALUES];
//		for (int i = 0; i < EngineConst.MAX_INDEX_VALUES; i++)
//			this.indexValues[i] = info.getIndexValue(i);
	}

	@Override
	public String getUri() {
		return info.getUri();
	}

	@Override
	public String getCaseCanonicalName() {
		return info.getCanonicalName();
	}

	@Override
	public String getIndexValue(int index) {
		return info.getIndexValue(index);
	}
	
	@Override
	public String getCustomId() {
		return info.getCustomId();
	}
	
	@Override
	public STATE_CASE getState() {
		return info.getState();
	}
	
	@Override
	public UUID getId() {
		return info.getId();
	}
	
	private synchronized void initProcess() {
		if (process != null) return;
		try {
			process = ui.getProcess(info.getUri());
		} catch (MException e) {
			log().d(info.getUri(),e);
		}
	}

	@Override
	public String getDisplayName() {
		initProcess();
		return process.getDisplayName(info.getUri(), null);
	}

	@Override
	public String getDescription() {
		initProcess();
		return process.getDescription(info.getUri(), null);
	}

}
