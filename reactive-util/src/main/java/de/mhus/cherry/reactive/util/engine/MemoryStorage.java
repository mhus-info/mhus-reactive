package de.mhus.cherry.reactive.util.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.model.engine.StorageProvider;
import de.mhus.lib.errors.NotFoundException;

public class MemoryStorage implements StorageProvider {

	protected HashMap<UUID, PCase> cases = new HashMap<>();
	protected HashMap<UUID, PNode> flowNodes = new HashMap<>();
	private PEngine engine;
	
	@Override
	public String toString() {
		return "Storage: \n" + engine + "\n" + cases + "\n" + flowNodes;
	}
	
	@Override
	public void saveCase(PCase caze) throws IOException {
		cases.put(caze.getId(), new MyEngineCase(caze));
	}

	@Override
	public PCase loadCase(UUID id) throws IOException, NotFoundException {
		return cases.get(id);
	}

	@Override
	public void deleteCaseAndFlowNodes(UUID id) throws IOException {
		cases.remove(id);
		flowNodes.entrySet().removeIf(entry -> id.equals(entry.getValue().getCaseId()));
	}

	@Override
	public void saveFlowNode(PNode flow) throws IOException {
		flowNodes.put(flow.getId(), new MyFlowNode(flow));
	}

	@Override
	public PNode loadFlowNode(UUID id) throws IOException, NotFoundException {
		return flowNodes.get(id);
	}

	@Override
	public Result<PCaseInfo> getCases(STATE_CASE state) throws IOException {
		ResultList<PCaseInfo> out = new ResultList<>();
		cases.values().forEach(entry -> {if (state == null || entry.getState() == state) out.add(new PCaseInfo(entry.getId(),entry.getUri())); } );
		return out;
	}

	@Override
	public Result<PNodeInfo> getFlowNodes(UUID caseId,
	        de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE state) throws IOException {
		ResultList<PNodeInfo> out = new ResultList<>();
		flowNodes.values().forEach(value -> {
			if ( (caseId == null || value.getCaseId().equals(caseId))
					&& 
				 (state == null || value.getState() == state)
				) out.add(new PNodeInfo(value.getId(),value.getCaseId()) ); });
		return out;
	}

	private static class MyEngineCase extends PCase {
		
		MyEngineCase(PCase clone) {
			super(clone);
		}
				
	}
	
	private static class MyFlowNode extends PNode {
		
		MyFlowNode(PNode clone) {
			super(clone);
			startState = state; // update state
		}
		
	}

	@Override
	public Result<PNodeInfo> getScheduledFlowNodes(de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE state,
	        long scheduled) throws IOException {
		ResultList<PNodeInfo> out = new ResultList<>();
		flowNodes.values().forEach(value -> {
			Entry<String, Long> entry = value.getNextScheduled();
			if ( 	entry != null &&
					(state == null || state == value.getState()) 
					&&
					entry.getValue() > 0 && entry.getValue() <= scheduled)
				out.add(new PNodeInfo(value.getId(),value.getCaseId()));
		});
		return out;
	}

	@Override
	public Result<PNodeInfo> getSignaledFlowNodes(de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE state,
	        String signal) throws IOException {
		ResultList<PNodeInfo> out = new ResultList<>();
		String intSig = PNode.getSignalAsString(signal);
		flowNodes.values().forEach(value -> {
			if (	(state == null || value.getState() == state) 
					&& 
					value.getSignalsAsString().contains(intSig)
				)
				out.add(new PNodeInfo(value.getId(),value.getCaseId()));
		});
		return out;
	}

	@Override
	public Result<PNodeInfo> getMessageFlowNodes(UUID caseId,
	        de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE state, String message) throws IOException {
		ResultList<PNodeInfo> out = new ResultList<>();
		String intSig = PNode.getMessageAsString(message);
		flowNodes.values().forEach(value -> {
			if (	(caseId == null || caseId.equals(value.getCaseId()))
					&&
					(state == null || value.getState() == state) 
					&& 
					value.getSignalsAsString().contains(intSig)
				)
				out.add(new PNodeInfo(value.getId(),value.getCaseId()));
		});
		return out;
	}

	@Override
	public PEngine loadEngine() {
		return engine;
	}

	@Override
	public void saveEngine(PEngine engine) {
		this.engine = new PEngine(engine);
	}
}
