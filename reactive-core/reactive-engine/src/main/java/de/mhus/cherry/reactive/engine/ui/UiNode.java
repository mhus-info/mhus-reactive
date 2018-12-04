/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.engine.ui;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AUserTask;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.annotations.generic.Public;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.AccessDeniedException;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.DefaultFormInformation;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;

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
	public IFormInformation getUserForm() {
		// TODO check assign
		try {
			engine.assignUserTask(info.getId(), ui.getUser());
		} catch (IOException | MException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initANode();
		AUserTask<?> un = (AUserTask<?>)aNode;
		return new DefaultFormInformation(un.getForm(), un.getActionHandler(), un.getFormControl());
	}

	@Override
	public IProperties getUserFormValues() throws MException {
		// TODO check assign
		initANode();
		return ((AUserTask<?>)aNode).getFormValues();
	}
	
	@Override
	public void submitUserTask(IProperties values) throws IOException, MException {
		// TODO check assign
		engine.submitUserTask(info.getId(), values);
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

	@Override
	public String getAssigned() {
		return info.getAssigned();
	}

	@Override
	public void doUnassign() throws IOException, MException {
		if (!engine.hasExecuteAccess(info.getId(), ui.getUser()))
			throw new AccessDeniedException();
		engine.unassignUserTask(info.getId());
	}

	@Override
	public void doAssign() throws IOException, MException {
		if (!engine.hasExecuteAccess(info.getId(), ui.getUser()))
			throw new AccessDeniedException();
		engine.assignUserTask(info.getId(), ui.getUser());
	}

	@Override
	public String getActor() {
		return info.getActor();
	}

	@Override
	public MProperties onUserTaskAction(MProperties values, String action) throws IOException, MException {
		return engine.onUserTaskAction(info.getId(), values, action);
	}

	@Override
	public Class <? extends FormControl> getUserFormControl() {
		initANode();
		return ((AUserTask<?>)aNode).getFormControl();
	}

}
