package de.mhus.cherry.reactive.util.activity;

import java.util.LinkedList;
import java.util.UUID;

import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.engine.EEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.util.ActivityUtil;

/**
 * Wait for all inputs and then execute all Outputs in parallel.
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public class RGatewayJoin<P extends RPool<?>> extends RGateway<P> {

	@Override
	public void initializeActivity() throws Exception {

		ProcessContext<P> context = getContext();
		EEngine engine = context.getEEngine();
		// by default set to wait
		context.getPNode().setState(STATE_NODE.WAITING);
		
		// all inputs are done?
		int size = ActivityUtil.getInputs(this).length;
		
		// find waiting gateways
		String myName = context.getPNode().getCanonicalName();
		UUID myId = context.getPNode().getId();
		LinkedList<UUID> current = new LinkedList<>();
		for ( PNodeInfo info : engine.storageGetFlowNodes(context.getPNode().getCaseId(), STATE_NODE.WAITING)) {
			if (info.getCanonicalName().equals(myName) && !info.getId().equals(myId) /* paranoia */) 
				current.add(info.getId());
		}
		
		if (current.size() >= (size-1) ) { // decrease 1 for myself
			// close all others, activate me !!!
			context.getPNode().setState(STATE_NODE.RUNNING);
			for (UUID id : current) {
				PNode node = engine.getFlowNode(id);
				node.setState(STATE_NODE.CLOSED);
				engine.saveFlowNode(node);
			}
		}
		
	}

	
	@Override
	public Output[] doExecute() throws Exception {
		return ActivityUtil.getOutputs(this);
	}

}
