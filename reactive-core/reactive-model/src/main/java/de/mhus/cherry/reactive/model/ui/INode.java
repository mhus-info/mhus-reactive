package de.mhus.cherry.reactive.model.ui;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;

public interface INode {

	String getUri();

	String getNodeCanonicalName();

	STATE_NODE getNodeSate();

	UUID getId();

	String getCustomId();

	String getCustomerId();

	String getIndexValue(int index);

	String getIndexDisplayName(int index);

	String getDisplayName();

	String getDescription();

}
