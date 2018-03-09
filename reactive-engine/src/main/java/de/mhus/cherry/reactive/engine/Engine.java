package de.mhus.cherry.reactive.engine;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.EngineCase;
import de.mhus.cherry.reactive.model.engine.EngineElement;
import de.mhus.cherry.reactive.model.engine.EngineFlowNode;
import de.mhus.cherry.reactive.model.engine.EngineFlowNode.STATE;
import de.mhus.cherry.reactive.model.engine.EnginePool;
import de.mhus.cherry.reactive.model.engine.EngineProcess;
import de.mhus.cherry.reactive.model.engine.ProcessProvider;
import de.mhus.cherry.reactive.model.engine.StorageProvider;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public class Engine {

	private StorageProvider storage;
	private StorageProvider archive;
	private ProcessProvider processProvider;

	public Engine(EngineConfiguration config) {
		storage = config.storage;
		archive = config.archive;
		processProvider = config.processProvider;
	}
	
	public void start(String process, IProperties properties) throws MException {
		String[] parts = process.split("/");
		EngineProcess p = processProvider.getProcess(parts[0]);
		if (p == null) throw new NotFoundException("process unknown",parts[0]);
		EnginePool pool = p.getPool(parts[1]);
		if (pool == null) throw new NotFoundException("pool not found in process",parts[0],parts[1]);
		
		EngineCase caze = createCase(pool,properties);
		for (EngineElement start : pool.getStartPoints()) {
			createFlowNode(caze, start);
		}
	}

	private void createFlowNode(EngineCase caze, EngineElement start) {
		
	}

	private EngineCase createCase(EnginePool pool, IProperties properties) {
		
		return null;
	}
	
	public void step() throws IOException {
		clearCaches();
		for ( EngineFlowNode ready : storage.getFlowNodes(STATE.READY)) {
			doFlowNode(ready);
		}
		for (EngineFlowNode scheduled : storage.getFlowNodes(STATE.SCHEDULED)) {
//			if (scheduled.)
//			doFlowNode(ready);
		}
		
	}

	private void clearCaches() {
		// TODO Auto-generated method stub
		
	}

	private void doFlowNode(EngineFlowNode ready) {
		EngineCase caze = getCase(ready.getCaseId());
		if (caze.getState() != EngineCase.STATE.READY) return;
		
	}

	private EngineCase getCase(UUID caseId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
