package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.annotations.vaadin.Column;

public class NodeItem {

	private INode node;

	public NodeItem(INode node) {
		this.node = node;
	}

	@Column(order=16,title="URI", editable=false,elapsed=false)
	public String getUri() {
		return node.getUri();
	}

	@Column(order=15,title="CName", editable=false,elapsed=false)
	public String getNodeCanonicalName() {
		return node.getNodeCanonicalName();
	}

	@Column(order=3,title="Name", editable=false)
	public STATE_NODE getNodeSate() {
		return node.getNodeSate();
	}

	@Column(order=14,title="Id", editable=false,elapsed=false)
	public UUID getId() {
		return node.getId();
	}

	@Column(order=1,title="Name", editable=false)
	public String getCustomId() {
		return node.getCustomId();
	}

	@Column(order=2,title="Name", editable=false)
	public String getDisplayName() {
		return node.getDisplayName();
	}

	@Column(order=17,title="Description", editable=false,elapsed=false)
	public String getDescription() {
		return node.getDescription();
	}

	@Column(order=4,title="Index 1", editable=false)
	public String getIndex0() {
		return node.getIndexValue(0);
	}

	@Column(order=5,title="Index 2", editable=false)
	public String getIndex1() {
		return node.getIndexValue(0);
	}

	@Column(order=6,title="Index 3", editable=false)
	public String getIndex2() {
		return node.getIndexValue(0);
	}

	@Column(order=7,title="Index 4", editable=false)
	public String getIndex3() {
		return node.getIndexValue(0);
	}

	@Column(order=8,title="Index 5", editable=false)
	public String getIndex4() {
		return node.getIndexValue(0);
	}

	@Column(order=9,title="Index 6", editable=false)
	public String getIndex5() {
		return node.getIndexValue(0);
	}

	@Column(order=10,title="Index 7", editable=false)
	public String getIndex6() {
		return node.getIndexValue(0);
	}

	@Column(order=11,title="Index 8", editable=false)
	public String getIndex7() {
		return node.getIndexValue(0);
	}

	@Column(order=12,title="Index 9", editable=false)
	public String getIndex8() {
		return node.getIndexValue(0);
	}

	@Column(order=13,title="Index 10", editable=false)
	public String getIndex9() {
		return node.getIndexValue(0);
	}

}
