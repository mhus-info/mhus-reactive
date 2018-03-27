package de.mhus.cherry.reactive.engine.ui;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.annotations.generic.Public;
import de.mhus.lib.core.MLog;

public class UiNode extends MLog implements INode {

	private PNodeInfo info;
	private Map<String, String> properties;

	public UiNode(PNodeInfo info, Map<String, String> properties) {
		this.info =info;
		this.properties = properties;
	}

	@Override
	@Public
	public String getUri() {
		return info.getUri();
	}

	@Override
	@Public
	public String getCanonicalName() {
		return info.getCanonicalName();
	}

	@Override
	@Public
	public STATE_NODE getNodeState() {
		return info.getState();
	}

	@Override
	@Public
	public UUID getId() {
		return info.getId();
	}

	@Override
	@Public
	public String getCustomId() {
		return info.getCustomId();
	}

	@Override
	@Public
	public String getCustomerId() {
		return info.getCustomerId();
	}

	@Override
	@Public
	public TYPE_NODE getType() {
		return info.getType();
	}

	@Override
	@Public
	public UUID getCaseId() {
		return info.getCaseId();
	}

	@Override
	@Public
	public Map<String, String> getProperties() {
		return properties;
	}

}
