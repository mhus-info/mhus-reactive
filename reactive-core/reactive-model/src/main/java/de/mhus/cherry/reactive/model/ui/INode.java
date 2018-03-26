package de.mhus.cherry.reactive.model.ui;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;

public interface INode {

	String getUri();

	String getNodeCanonicalName();

	STATE_NODE getNodeState();

	UUID getId();

	String getCustomId();

	String getCustomerId();

	String getIndexValue(int index);

	String getIndexDisplayName(int index);

	String getDisplayName();

	String getDescription();

	TYPE_NODE getType();

	UUID getCaseId();

}
