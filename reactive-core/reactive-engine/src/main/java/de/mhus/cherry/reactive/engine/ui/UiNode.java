package de.mhus.cherry.reactive.engine.ui;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

public class UiNode extends MLog implements INode {

	private IEngine ui;
	private PNodeInfo info;
	private IProcess process;

	public UiNode(IEngine ui, PNodeInfo info) {
		this.ui = ui;
		this.info =info;
	}

	@Override
	public String getUri() {
		return info.getUri();
	}

	@Override
	public String getNodeCanonicalName() {
		return info.getCanonicalName();
	}

	@Override
	public STATE_NODE getNodeState() {
		return info.getState();
	}

	@Override
	public UUID getId() {
		return info.getId();
	}

	@Override
	public String getCustomId() {
		return info.getCustomId();
	}

	@Override
	public String getIndexValue(int index) {
		return info.getIndexValue(index);
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
		return process.getDisplayName(info.getUri(), info.getCanonicalName());
	}

	@Override
	public String getDescription() {
		initProcess();
		return process.getDescription(info.getUri(), info.getCanonicalName());
	}

	@Override
	public String getIndexDisplayName(int index) {
		initProcess();
		return process.getIndexDisplayName(index, info.getUri(), info.getCanonicalName());
	}

	@Override
	public String getCustomerId() {
		return info.getCustomerId();
	}

	@Override
	public TYPE_NODE getType() {
		return info.getType();
	}

	@Override
	public UUID getCaseId() {
		return info.getCaseId();
	}

}
