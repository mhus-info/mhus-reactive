package de.mhus.cherry.reactive.engine.ui;

import java.util.UUID;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.EngineMessage;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.cherry.reactive.model.ui.IModel;
import de.mhus.cherry.reactive.model.ui.INodeDescription;
import de.mhus.lib.core.util.MUri;

public class UiModel implements IModel {

	private UUID nodeId;
	private INodeDescription[] outputs;
	private EngineMessage[] messages;
	private INodeDescription predecessor;
	private INodeDescription node;

	public UiModel(UiEngine ui, Engine engine, UUID nodeId) throws Exception {
		this.nodeId = nodeId;
		PNode pNode = engine.getFlowNode(nodeId);
		PCase caze = engine.getCase(pNode.getCaseId());
		MUri uri = MUri.toUri(caze.getUri());
		EProcess process = engine.getProcess(uri);
		EPool pool = engine.getPool(process, uri);
		String uriStr = "bpm://" + process.getCanonicalName() + "/" + pool.getCanonicalName();
		node = ui.getNodeDescription(uriStr, pNode.getCanonicalName());
		EElement element = pool.getElement(pNode.getCanonicalName());
		ActivityDescription desc = element.getActivityDescription();
		
		Output[] out = desc.outputs();
		outputs = new INodeDescription[out.length];
		for (int i = 0; i < out.length; i++)
			outputs[i] = ui.getNodeDescription(uriStr,out[i].activity().getCanonicalName());
		
		EngineContext context = engine.createContext(caze, pNode);
		PNode pRuntime = engine.getRuntimeForPNode(context, pNode);
		RuntimeNode aRuntime = engine.createRuntimeObject(context, pRuntime);

		messages = aRuntime.getMessages().toArray(new EngineMessage[0]);
		
		for ( EngineMessage msg : messages) {
			if (msg.getToNode() != null && msg.getFromNode() != null && msg.getToNode().equals(nodeId)) {
				PNodeInfo predecessorNode = engine.getFlowNodeInfo(msg.getFromNode());
				predecessor = ui.getNodeDescription(uriStr, predecessorNode.getCanonicalName());
				break;
			}
		}
		
	}

	@Override
	public INodeDescription getPredecessor() {
		return predecessor;
	}

	@Override
	public INodeDescription[] getOutputs() {
		return outputs;
	}

	@Override
	public INodeDescription getNode() {
		return node;
	}

	@Override
	public UUID getNodeId() {
		return nodeId;
	}

	@Override
	public EngineMessage[] getRuntimeMessages() {
		return messages;
	}

}
