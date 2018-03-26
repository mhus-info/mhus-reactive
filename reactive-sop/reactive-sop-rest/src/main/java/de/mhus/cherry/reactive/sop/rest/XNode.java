package de.mhus.cherry.reactive.sop.rest;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.annotations.generic.Public;

public class XNode {

	private INode item;
	private IEngine engine;
	private boolean extended;

	public XNode(IEngine engine, INode item, boolean extended) {
		this.item = item;
		this.engine = engine;
		this.extended = extended;
	}

	@Public
	public String getUri() {
		return item.getUri();
	}

	@Public
	public String getCanonicalName() {
		return item.getNodeCanonicalName();
	}

	@Public
	public String getIndexValue0() {
		return item.getIndexValue(0);
	}

	@Public
	public String getIndexValue1() {
		return item.getIndexValue(1);
	}
	
	@Public
	public String getIndexValue2() {
		return item.getIndexValue(2);
	}

	@Public
	public String getIndexValue3() {
		return item.getIndexValue(3);
	}

	@Public
	public String getIndexValue4() {
		return item.getIndexValue(4);
	}

	@Public
	public String getIndexValue5() {
		return item.getIndexValue(5);
	}

	@Public
	public String getIndexValue6() {
		return item.getIndexValue(6);
	}

	@Public
	public String getIndexValue7() {
		return item.getIndexValue(7);
	}

	@Public
	public String getIndexValue8() {
		return item.getIndexValue(8);
	}

	@Public
	public String getIndexValue9() {
		return item.getIndexValue(9);
	}

	@Public
	public String getCustomId() {
		return item.getCustomId();
	}

	@Public
	public String getCustomerId() {
		return item.getCustomerId();
	}

	@Public
	public STATE_NODE getState() {
		return item.getNodeState();
	}

	@Public
	public TYPE_NODE getType() {
		return item.getType();
	}
	
	@Public
	public UUID getId() {
		return item.getId();
	}
	
	@Public
	public UUID getCaseId() {
		return item.getCaseId();
	}
	
}
