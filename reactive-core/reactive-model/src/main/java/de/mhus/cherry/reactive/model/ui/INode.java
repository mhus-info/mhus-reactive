package de.mhus.cherry.reactive.model.ui;

import java.util.UUID;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public interface INode {

	String getUri();

	String getNodeCanonicalName();

	STATE_NODE getNodeSate();

	UUID getId();

	String getCustomId();

	String getIndexValue(int index);

	String getDisplayName();

	String getDescription();

}
