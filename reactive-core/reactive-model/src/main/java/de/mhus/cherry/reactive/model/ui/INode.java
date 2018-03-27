package de.mhus.cherry.reactive.model.ui;

import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;

public interface INode {

	String getUri();

	String getCanonicalName();

	STATE_NODE getNodeState();

	UUID getId();

	String getCustomId();

	String getCustomerId();

	TYPE_NODE getType();

	UUID getCaseId();

	Map<String,String> getProperties();
	
}
