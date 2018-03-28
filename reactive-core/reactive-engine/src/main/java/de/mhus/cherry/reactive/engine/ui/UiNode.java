package de.mhus.cherry.reactive.engine.ui;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AHumanTask;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.lib.annotations.generic.Public;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

public class UiNode extends MLog implements INode {

	private PNodeInfo info;
	private Map<String, String> properties;
	private Engine engine;
	private AElement<?> aNode;
	private UiEngine ui;

	public UiNode(Engine engine, UiEngine ui,PNodeInfo info, Map<String, String> properties) {
		this.engine = engine;
		this.info =info;
		this.properties = properties;
		this.ui = ui;
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

	@Override
	@Public
	public long getCreated() {
		return info.getCreated();
	}

	@Override
	@Public
	public long getModified() {
		return info.getModified();
	}

	@Override
	@Public
	public int getPriority() {
		return info.getPriority();
	}

	@Override
	@Public
	public int getScore() {
		return info.getScore();
	}

	@Override
	public HumanForm getHumanForm() {
		// TODO check assign
		try {
			engine.assignHumanTask(info.getId(), ui.getUser());
		} catch (IOException | MException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initANode();
		return ((AHumanTask<?>)aNode).createForm();
	}

	@Override
	public IProperties getHumanFormValues() throws MException {
		// TODO check assign
		initANode();
		return ((AHumanTask<?>)aNode).getFormValues();
	}
	
	@Override
	public void submitHumanTask(IProperties values) throws IOException, MException {
		// TODO check assign
		engine.submitHumanTask(info.getId(), values);
	}
	
	

	private synchronized void initANode() {
		if (aNode != null) return;
		try {
			PNode node = engine.getFlowNode(info.getId());
			PCase caze = engine.getCase(node.getCaseId());
			EngineContext context = engine.createContext(caze, node);
			aNode = context.getANode();
		} catch (Throwable t) {
			log().e(t);
		}
	}

}
