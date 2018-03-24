package de.mhus.cherry.reactive.engine.ui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.ui.IProcess;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.core.util.SoftHashMap;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public class UiEngine extends MLog implements IEngine {

	private Engine engine;
	private String user;
	private SoftHashMap<String, Boolean> cacheAccessRead = new SoftHashMap<>();
	private SoftHashMap<String, Boolean> cacheAccessWrite = new SoftHashMap<>();
	private SoftHashMap<UUID, Boolean> cacheAccessExecute = new SoftHashMap<>();
	private SoftHashMap<String, EngineContext> cacheContext = new SoftHashMap<>();

	public UiEngine(Engine engine, String user) {
		this.engine = engine;
		this.user = user;
	}
	
	@Override
	public List<INode> searchNodes(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException {
		LinkedList<INode> out = new LinkedList<>();
		int cnt = 0;
		int first = page * size;
		for (PNodeInfo info : engine.storageSearchFlowNodes(criterias)) {
			if (user.equals(info.getAssigned()) || hasReadAccess(info.getUri())) {
				try {
					if (cnt >= first) {
						out.add(new UiNode(this,info));
					}
					cnt++;
				} catch (Exception e) {
					log().d(info,e);
				}
				if (out.size() >= size) break;
			}
		}
		return out;
	}

	@Override
	public List<ICase> searchCases(SearchCriterias criterias, int page, int size) throws NotFoundException, IOException {
		LinkedList<ICase> out = new LinkedList<>();
		int cnt = 0;
		int first = page * size;
		for (PCaseInfo info : engine.storageSearchCases(criterias)) {
			if (hasReadAccess(info.getUri())) {
				try {
					if (cnt >= first) {
						out.add(new UiCase(this, info));
					}
					cnt++;
				} catch (Exception e) {
					log().d(info,e);
				}
				if (out.size() >= size) break;
			}
		}
		return out;
	}

	private EngineContext getContext(String uri) {
		synchronized (cacheContext) {
			EngineContext context = cacheContext.get(uri);
			if (context != null) return context;
		}
		MUri muri = MUri.toUri(uri);
		try {
			EProcess process = engine.getProcess(muri);
			EPool pool = engine.getPool(process, muri);
			EngineContext context = new EngineContext(engine);
			context.setEProcess(process);
			context.setEPool(pool);
			synchronized (cacheContext) {
				cacheContext.put(uri, context);
			}
			return context;
		} catch (Throwable t) {
			log().e(uri,user,t);
			return null;
		}
	}

	public boolean hasReadAccess(String uri) {	
		synchronized (cacheAccessRead) {
			Boolean hasAccess = cacheAccessRead.get(uri);
			if (hasAccess != null) return hasAccess;
		}

		boolean hasAccess = engine.hasReadAccess(uri, user);
		synchronized (cacheAccessRead) {
			cacheAccessRead.put(uri,hasAccess);
		}
		return hasAccess;
	}
		
	public boolean hasWriteAccess(String uri) {	
		synchronized (cacheAccessWrite) {
			Boolean hasAccess = cacheAccessWrite.get(uri);
			if (hasAccess != null) return hasAccess;
		}

		boolean hasAccess = engine.hasWriteAccess(uri, user);
		synchronized (cacheAccessWrite) {
			cacheAccessWrite.put(uri,hasAccess);
		}
		return hasAccess;
	}

	public boolean hasWriteAccess(UUID nodeId) {	
		synchronized (cacheAccessExecute) {
			Boolean hasAccess = cacheAccessExecute.get(nodeId);
			if (hasAccess != null) return hasAccess;
		}

		boolean hasAccess = engine.hasExecuteAccess(nodeId, user);
		synchronized (cacheAccessExecute) {
			cacheAccessExecute.put(nodeId,hasAccess);
		}
		return hasAccess;
	}

	@Override
	public IProcess getProcess(String uri) throws MException {
		return new UiProcess(engine.getProcess(MUri.toUri(uri)));
	}

	@Override
	public ICase getCase(UUID id) throws Exception {
		return new UiCase(this, engine.storageGetCaseInfo(id));
	}
	
	@Override
	public INode getNode(UUID id) throws Exception {
		return new UiNode(this, engine.storageGetFlowNodeInfo(id));
	}
	
}
