package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.annotations.vaadin.Column;

public class NodeItem {

	private INode node;

	public NodeItem(INode node) {
		this.node = node;
	}

	@Column(order=1,title="id", editable=false,elapsed=false)
	public UUID getId() {
		return node.getId();
	}
		
	@Column(order=2,title="CName", editable=false)
	public String getNodeCanonicalName() {
		return node.getCanonicalName();
	}

	@Column(order=3,title="CutsomId", editable=false)
	public String getCustomId() {
		return node.getCustomId();
	}

	@Column(order=4,title="URI", editable=false, elapsed=false)
	public String getUri() {
		return node.getUri();
	}
	
	@Column(order=5,title="Index 1", editable=false)
	public String getIndex0() {
		return node.getProperties().get("pnode.index0");
	}

	@Column(order=6,title="Index 2", editable=false)
	public String getIndex1() {
		return node.getProperties().get("pnode.index1");
	}

	@Column(order=7,title="Index 3", editable=false)
	public String getIndex2() {
		return node.getProperties().get("pnode.index2");
	}

	@Column(order=8,title="Index 4", editable=false, elapsed=false)
	public String getIndex3() {
		return node.getProperties().get("pnode.index3");
	}

	@Column(order=9,title="Index 5", editable=false, elapsed=false)
	public String getIndex4() {
		return node.getProperties().get("pnode.index4");
	}

	@Column(order=10,title="Index 6", editable=false, elapsed=false)
	public String getIndex5() {
		return node.getProperties().get("pnode.index5");
	}

	@Column(order=11,title="Index 7", editable=false, elapsed=false)
	public String getIndex6() {
		return node.getProperties().get("pnode.index6");
	}

	@Column(order=12,title="Index 8", editable=false, elapsed=false)
	public String getIndex7() {
		return node.getProperties().get("pnode.index7");
	}

	@Column(order=13,title="Index 9", editable=false, elapsed=false)
	public String getIndex8() {
		return node.getProperties().get("pnode.index8");
	}

	@Column(order=14,title="Index 10", editable=false, elapsed=false)
	public String getIndex9() {
		return node.getProperties().get("pnode.index9");
	}
	
	@Column(order=15,title="State", editable=false)
	public STATE_NODE getState() {
		return node.getNodeState();
	}
	
	@Column(order=16,title="Type", editable=false)
	public TYPE_NODE getType() {
		return node.getType();
	}

	@Column(order=17,title="Customer", editable=false)
	public String getCustomer() {
		return node.getCustomerId();
	}

	/*	
	@Column(order=2,title="URI", editable=false,elapsed=false)
	public String getUri() {
		return node.getUri();
	}

	@Column(order=3,title="CName", editable=false,elapsed=false)
	public String getNodeCanonicalName() {
		return node.getCanonicalName();
	}


	@Column(order=4,title="Id", editable=false,elapsed=false)
	public UUID getId() {
		return node.getId();
	}

	@Column(order=5,title="CutsomId", editable=false)
	public String getCustomId() {
		return node.getCustomId();
	}

	@Column(order=6,title="Index 1", editable=false)
	public String getIndex0() {
		return node.getProperties().get("pnode.index0");
	}

	@Column(order=7,title="Index 2", editable=false)
	public String getIndex1() {
		return node.getProperties().get("pnode.index1");
	}

	@Column(order=8,title="Index 3", editable=false)
	public String getIndex2() {
		return node.getProperties().get("pnode.index2");
	}

	@Column(order=9,title="Index 4", editable=false)
	public String getIndex3() {
		return node.getProperties().get("pnode.index3");
	}

	@Column(order=10,title="Index 5", editable=false)
	public String getIndex4() {
		return node.getProperties().get("pnode.index4");
	}

	@Column(order=11,title="Index 6", editable=false)
	public String getIndex5() {
		return node.getProperties().get("pnode.index5");
	}

	@Column(order=12,title="Index 7", editable=false)
	public String getIndex6() {
		return node.getProperties().get("pnode.index6");
	}

	@Column(order=13,title="Index 8", editable=false)
	public String getIndex7() {
		return node.getProperties().get("pnode.index7");
	}

	@Column(order=14,title="Index 9", editable=false)
	public String getIndex8() {
		return node.getProperties().get("pnode.index8");
	}

	@Column(order=15,title="Index 10", editable=false)
	public String getIndex9() {
		return node.getProperties().get("pnode.index9");
	}
*/
	
	@Override
	public boolean equals(Object in) {
		if (in == null || !(in instanceof NodeItem)) return false;
		return node.getId().equals( ((NodeItem)in).getId() );
	}

}
