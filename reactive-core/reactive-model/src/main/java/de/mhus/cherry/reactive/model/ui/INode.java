package de.mhus.cherry.reactive.model.ui;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.errors.MException;

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

	long getCreated();

	long getModified();

	int getPriority();

	int getScore();

	HumanForm getHumanForm();

	IProperties getHumanFormValues() throws MException;

	void submitHumanTask(IProperties values) throws IOException, MException;
	
}
