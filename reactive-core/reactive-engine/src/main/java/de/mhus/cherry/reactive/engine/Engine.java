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
package de.mhus.cherry.reactive.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.WeakHashMap;

import de.mhus.cherry.reactive.engine.util.EngineListenerUtil;
import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.AStartPoint;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.activity.AUserTask;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.engine.AaaProvider;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EEngine;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.engine.EngineListener;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.engine.ProcessProvider;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.engine.StorageProvider;
import de.mhus.cherry.reactive.model.errors.EngineException;
import de.mhus.cherry.reactive.model.errors.TaskException;
import de.mhus.cherry.reactive.model.errors.TechnicalException;
import de.mhus.cherry.reactive.model.migrate.Migrator;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.model.util.CloseActivity;
import de.mhus.cherry.reactive.model.util.InactiveStartPoint;
import de.mhus.cherry.reactive.model.util.IndexValuesProvider;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.core.MValidator;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.core.util.MutableUri;
import de.mhus.lib.core.util.SoftHashMap;
import de.mhus.lib.errors.AccessDeniedException;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.errors.TimeoutRuntimeException;
import de.mhus.lib.errors.UsageException;

public class Engine extends MLog implements EEngine, InternalEngine {

	private StorageProvider storage;
	private StorageProvider archive;
	private ProcessProvider processProvider;
	private SoftHashMap<UUID,PCase> caseCache = new SoftHashMap<>();
	private SoftHashMap<UUID,PNode> nodeCache = new SoftHashMap<>();
	private HashSet<UUID> executing = new HashSet<>();
	private EngineConfiguration config;
	private EngineListener fireEvent = null;
	private WeakHashMap<UUID, Object> cacheCaseLock = new WeakHashMap<>();

	public Engine(EngineConfiguration config) {
		this.config = config;
		fireEvent = EngineListenerUtil.createEngineEventProcessor(config);
		storage = config.storage;
		archive = config.archive;
		processProvider = config.processProvider;
		
		try {
			config.persistent = storage.loadEngine();
		} catch (NotFoundException | IOException e) {
			log().i(e.toString());
		}
		if (config.persistent == null) {
			log().i("Initial new engine persistence");
			config.persistent = new PEngine();
		}
		if (config.parameters != null) {
			config.persistent.getParameters().putAll(config.parameters);
			try {
				storage.saveEngine(config.persistent);
			} catch (IOException e) {
				log().e(e);
			}
		}

	}
	
// ---
	
	public void step() throws IOException, NotFoundException {
		processNodes();
		cleanupCases();
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public int processNodes() throws IOException, NotFoundException {
		
		int doneCnt = 0;
		
		// Init
		long now = System.currentTimeMillis();

		// SCHEDULED NODES
		fireEvent.doStep("scheduled");
		for (PNodeInfo nodeId : storage.getScheduledFlowNodes(STATE_NODE.SCHEDULED, now)) {
			PNode node = getFlowNode(nodeId.getId());
			// set state back to ready
			fireEvent.setScheduledToRunning(node);
			synchronized (getCaseLock(node)) {
				node.setState(STATE_NODE.RUNNING);
				saveFlowNode(null, node, null);
			}
		}
		for (PNodeInfo nodeInfo : storage.getScheduledFlowNodes(STATE_NODE.WAITING, now)) {
			PCase caze = getCase(nodeInfo.getCaseId());
			if (caze.getState() == STATE_CASE.RUNNING)
				fireScheduledTrigger(nodeInfo);
			else
			if (caze.getState() == STATE_CASE.CLOSED) {
				// stop node also
				log().d("auto stop waiting node",nodeInfo);
				PNode node = getFlowNode(nodeInfo.getId());
				node.setSuspendedState(node.getState());
				node.setState(STATE_NODE.STOPPED);
				storage.saveFlowNode(node);
			}
		}

		// READY NODES
		fireEvent.doStep("execute");
		boolean parallel = MCast.toboolean(config.persistent.getParameters().get(EngineConst.ENGINE_EXECUTE_PARALLEL), true);
		Result<PNodeInfo> result = storage.getScheduledFlowNodes(STATE_NODE.RUNNING, now);
		if (parallel) {
			int maxThreads = MCast.toint(config.persistent.getParameters().get(EngineConst.ENGINE_EXECUTE_MAX_THREADS), 10);
			LinkedList<FlowNodeExecutor> threads = new LinkedList<>();
			for (PNodeInfo nodeInfo : result) {
				synchronized (executing) {
					if (executing.contains(nodeInfo.getId())) continue;
				}
				PNode node = getFlowNode(nodeInfo.getId());
				PCase caze = getCase(node.getCaseId());
				if (isProcessActive(caze)) {
					if (caze.getState() == STATE_CASE.RUNNING) {
						doneCnt++;
						FlowNodeExecutor executor = new FlowNodeExecutor(node);
						threads.add(executor);
						synchronized (executing) {
							executing.add(node.getId());
						}
						Thread thread = new Thread(executor);
						executor.thread = thread;
						thread.start();
						if (threads.size() >= maxThreads) break;
					} else
					if (caze.getState() == STATE_CASE.CLOSED) {
						// stop node also
						log().d("auto stop running node",nodeInfo);
						node.setSuspendedState(node.getState());
						node.setState(STATE_NODE.STOPPED);
						storage.saveFlowNode(node);
					}
				} 
			}
			
			while (threads.size() > 0) {
				threads.removeIf(e -> e.isFinished());
				MThread.sleep(200);
			}
			
			// sleep
			MThread.sleep(MCast.tolong(config.persistent.getParameters().get(EngineConst.ENGINE_SLEEP_BETWEEN_PROGRESS), 100));

		} else {
			for (PNodeInfo nodeId : result) {
				synchronized (executing) {
					if (executing.contains(nodeId)) continue;
				}
				PNode node = getFlowNode(nodeId.getId());
				PCase caze = getCase(node.getCaseId());
				if (isProcessActive(caze)) {
					if (caze.getState() == STATE_CASE.RUNNING) {
						synchronized (executing) {
							executing.add(nodeId.getId());
						}
						doneCnt++;
						doFlowNode(node);
						synchronized (executing) {
							executing.remove(nodeId);
						}
						// sleep
						MThread.sleep(MCast.tolong(config.persistent.getParameters().get(EngineConst.ENGINE_SLEEP_BETWEEN_PROGRESS), 100));

					} else
					if (caze.getState() == STATE_CASE.CLOSED) {
						// stop node also
						node.setSuspendedState(node.getState());
						node.setState(STATE_NODE.STOPPED);
						storage.saveFlowNode(node);
					}
				}
			}
		}
		result.close();
				
		fireEvent.doStep("execute finished");
		
		return doneCnt;
	}
	
	public boolean isProcessActive(PCase caze) {
		try {
			MUri uri = MUri.toUri(caze.getUri());
			EProcess process = getProcess(uri);
			if (process == null) return false;
			EPool pool = getPool(process, uri);
			if (pool == null) return false;
			return pool.getPoolClass() != null;
		} catch (Throwable t) {}
		return false;
	}

	public void cleanupCases() throws IOException, NotFoundException {
				
		// scan for closeable cases and runtimes
		fireEvent.doStep("cleanup");
		for (PCaseInfo caseInfo : storage.getCases(STATE_CASE.RUNNING)) {
			synchronized (getCaseLock(caseInfo)) {
				boolean found = false;
				HashSet<UUID> allRuntime = new HashSet<>();
				HashSet<UUID> usedRuntime = new HashSet<>();
				
				for ( PNodeInfo nodeId : storage.getFlowNodes(caseInfo.getId(), null)) {
					PNode node = getFlowNode(nodeId.getId());
					if (node.getType() == TYPE_NODE.RUNTIME && node.getState() == STATE_NODE.WAITING) {
						allRuntime.add(node.getId());
					} else
					if (node.getState() != STATE_NODE.CLOSED && node.getState() != STATE_NODE.ZOMBIE) {
						found = true;
						usedRuntime.add(node.getRuntimeId());
					}
				}
				
				// close unused runtimes
				allRuntime.removeIf(u -> usedRuntime.contains(u));
				for (UUID rId : allRuntime) {
					try {
						PNode runtime = getFlowNode(rId);
						closeRuntime(runtime);
					} catch (Throwable t) {
						log().e(rId,t);
						fireEvent.error(rId,t);
					}
				}
				if (!found) {
					// close case without active node
					PCase caze = getCase(caseInfo.getId());
					closeCase(caze, false, 0, "");
				}
			}
		}
		
		fireEvent.doStep("cleanup finished");
				
	}

	private class FlowNodeExecutor implements Runnable {

		public boolean finished = false;
		public boolean outtimed = false;
		@SuppressWarnings("unused")
		public Thread thread;
		private PNode node;
		long start = System.currentTimeMillis();

		public FlowNodeExecutor(PNode node) {
			this.node = node;
		}

		public boolean isFinished() {
			if (finished || outtimed) return true;
			try {
				if (MTimeInterval.isTimeOut(start, node.getActivityTimeout())) {
					fireEvent.error("activity timeout",node);
					outtimed = true;
					return true;
				}
			} catch (Throwable t) {
				t.printStackTrace(); // should not happen
			}
			return false;
		}

		@Override
		public void run() {
			start = System.currentTimeMillis();
			try {
				doFlowNode(node);
			} catch (Throwable t) {
				try {
					log().e(node,t);
					fireEvent.error(node,t);
				} catch (Throwable t2) {}
			}
			synchronized (executing) {
				executing.remove(node.getId());
			}
			finished = true;
		}
		
	}

	public List<UUID> getExecuting() {
		synchronized (executing) {
			return new LinkedList<UUID>(executing);
		}
	}
	
	public void resaveFlowNode(UUID nodeId) throws IOException, MException {
		PNode node = getFlowNode(nodeId);
		PCase caze = getCase(node.getCaseId());
		EngineContext context = createContext(caze, node);
		
		{
			node.getSchedulers().clear();
			HashMap<String, Long> list = context.getENode().getSchedulerList();
			if (list != null)
				node.getSchedulers().putAll(list);
		}
		{
			node.getMessageTriggers().clear();
			HashMap<String, String> list = context.getENode().getMessageList();
			if (list != null)
				node.getMessageTriggers().putAll(list);
		}
		{
			node.getSignalTriggers().clear();
			HashMap<String, String> list = context.getENode().getSignalList();
			if (list != null)
				node.getSignalTriggers().putAll(list);
		}
		storage.saveFlowNode(node);
	}
	
	public void resaveCase(UUID caseId) throws IOException, NotFoundException {
		PCase caze = getCase(caseId);
		storage.saveCase(caze);
	}
	
	@Override
	public void saveFlowNode(PNode flow) throws IOException, NotFoundException {
		fireEvent.saveFlowNode(flow,null);
		synchronized (getCaseLock(flow)) {
			synchronized (nodeCache) {
				storage.saveFlowNode(flow);
				nodeCache.put(flow.getId(), flow);
				flow.updateStartState();
			}
		}
	}
	
	private void saveFlowNode(EngineContext context, PNode flow, AActivity<?> activity) throws IOException {
		fireEvent.saveFlowNode(flow,activity);
		synchronized (getCaseLock(flow)) {
			if (activity != null) {
				try {
					Map<String, Object> newParameters = activity.exportParamters();
					flow.getParameters().putAll(newParameters);
					
					if (activity instanceof IndexValuesProvider) {
						flow.setIndexValues( ((IndexValuesProvider)activity).createIndexValues(false) );
					} else {
						flow.setIndexValues(null);
					}
					
				} catch (Throwable t) {
					log().e(t);
					fireEvent.error(flow,activity,t);
					// set failed
					flow.setSuspendedState(flow.getState());
					flow.setState(STATE_NODE.STOPPED);
				}
			}
			synchronized (nodeCache) {
				storage.saveFlowNode(flow);
				nodeCache.put(flow.getId(), flow);
				flow.updateStartState();
			}
		}
		if (context != null)
			savePCase(context);
	}

	public void closeCase(UUID caseId, boolean hard, int code, String msg) throws IOException, NotFoundException {
		PCase caze = getCase(caseId);
		closeCase(caze, hard, code, msg);
	}
	
	public void closeCase(PCase caze, boolean hard, int code, String msg) throws IOException {
		fireEvent.closeCase(caze,hard);
		EngineContext context = null;
		if (!hard) {
			try {
				MUri uri = MUri.toUri(caze.getUri());
				EProcess process = getProcess(uri);
				EPool pool = getPool(process, uri);
				
				context = new EngineContext(this);
				context.setUri(uri.toString());
				context.setEProcess(process);
				context.setEPool(pool);
				context.setPCase(caze);
		
				APool<?> aPool = context.getPool();
				aPool.closeCase();
				Map<String, Object> newParameters = aPool.exportParamters();
				caze.getParameters().putAll(newParameters);
				
				if (aPool instanceof IndexValuesProvider) {
					caze.setIndexValues( ((IndexValuesProvider)aPool).createIndexValues(false) );
				} else {
					caze.setIndexValues(null);
				}

			} catch (Throwable t) {
				log().e(caze,t);
				fireEvent.error(caze,t);
			}
		}
		synchronized (getCaseLock(caze)) {
			caze.close(code, msg);
			synchronized (caseCache) {
				storage.saveCase(caze);
				caseCache.put(caze.getId(), caze);
			}
		}
		
		if (!hard && caze.getCloseActivity() != null) {
			UUID closeId = caze.getCloseActivity();
			if (closeId != null) {
				try {
					doCloseActivity(context, closeId);
				} catch (Throwable t) {
					log().e(closeId,t);
				}
			}
		}
	}

	public void closeRuntime(PNode pNode) throws MException, IOException {
		fireEvent.closeRuntime(pNode);
		PCase caze = null;
		try {
			caze = getCase(pNode.getCaseId());
		} catch (Throwable t) {
			log().e(pNode.getCaseId(),t);
			fireEvent.error(pNode,t);
			return; // ignore - try next time
		}
			
		if (caze.getState() != STATE_CASE.RUNNING) {
			pNode.setScheduled(newScheduledTime(pNode));
			return;
		}
		
		// create context
		EngineContext context = createContext(caze, pNode);

		RuntimeNode aNode = createRuntimeObject(context, pNode);
		aNode.close();
		
		pNode.setState(STATE_NODE.CLOSED);
		saveRuntime(pNode, aNode);

		UUID closeId = aNode.getCloseActivity();
		if (closeId != null) {
			try {
				doCloseActivity(context, closeId);
			} catch (Throwable t) {
				log().e(closeId,t);
			}
		}
	}

	private void doFlowNode(PNode pNode) {
		fireEvent.doFlowNode(pNode);
		try {
			
			PCase caze = null;
			try {
				caze = getCase(pNode.getCaseId());
			} catch (Throwable t) {
				log().e(pNode.getCaseId(),t);
				fireEvent.error(pNode,t);
				return; // ignore - try next time
			}
			
			if (caze == null) {
				// node without case ... puh
				fireEvent.error("node without case",pNode,pNode.getCaseId());
				closeFlowNode(null, pNode, STATE_NODE.STOPPED);
				return;
			}
			synchronized (getCaseLock(caze)) {
				if (caze.getState() != STATE_CASE.RUNNING) {
					pNode.setScheduled(newScheduledTime(pNode));
					fireEvent.doFlowNodeScheduled(pNode);
					return;
				}
				
				// create context
				EngineContext context = createContext(caze, pNode);
	
				// check for timer trigger
				Entry<String, Long> nextScheduled = pNode.getNextScheduled();
				if (nextScheduled != null && !nextScheduled.getKey().equals("")) {
					// do trigger
					Trigger trigger = getTrigger(context,nextScheduled.getKey());
					if (trigger == null) {
						// set to error
						log().e("Unknown trigger",pNode,nextScheduled.getKey());
						fireEvent.error("Unknown trigger",pNode,nextScheduled.getKey());
						closeFlowNode(context, pNode, STATE_NODE.STOPPED);
						return;
					}
					Class<? extends AActivity<?>> next = trigger.activity();
					EElement eNext = context.getEPool().getElement(next.getCanonicalName());
					if (context.getARuntime().getConnectCount() > EngineConst.MAX_CREATE_ACTIVITY) {
						fireEvent.error("max activities reached",caze);
						closeCase(caze, true, EngineConst.ERROR_CODE_MAX_CREATE_ACTIVITY, "max activities reached");
						return;
					}
					createActivity(context, pNode, eNext);
					// close this
					closeFlowNode(context, pNode, STATE_NODE.CLOSED);
					return;
				}
				
				// do lifecycle
				doNodeLifecycle(context, pNode);
			}
		} catch (Throwable t) {
			log().e(pNode,t);
			fireEvent.error(pNode,t);
		}
	}

	public Trigger getTrigger(EngineContext context, String key) {
		
		if (key.startsWith("[")) {
			int index = MCast.toint(key.substring(1), -1);
			Trigger[] list = context.getENode().getTriggers();
			if (index < 0 || list.length <= index ) return null;
			return list[index];
		}
		for (Trigger trigger : context.getENode().getTriggers()) {
			if (trigger.name().equals(key))
				return trigger;
		}
		return null;
	}

	@Override
	public void doNodeErrorHandling(PNode closeNode, String error) throws Exception {
		PCase caze = getCase(closeNode.getCaseId());
		EngineContext context = createContext(caze, closeNode);
		doNodeErrorHandling(context, closeNode, new TaskException("syntetic",error));
	}

	private void doNodeErrorHandling(EngineContext context, PNode pNode, Throwable t) {
		fireEvent.doNodeErrorHandling(context,pNode,t);
		
		if (t instanceof TechnicalException) {
			try {
				closeFlowNode(context, pNode, STATE_NODE.FAILED);
			} catch (IOException e) {
				log().e(pNode,e);
				fireEvent.error(pNode,e);
			}
			return;
		}
		
		if (t instanceof EngineException) {
			try {
				closeFlowNode(context, pNode, STATE_NODE.STOPPED);
			} catch (IOException e) {
				log().e(pNode,e);
				fireEvent.error(pNode,e);
			}
			return;
		}
		
		EElement eNode = context.getENode();
		Trigger defaultError = null;
		Trigger errorHandler = null;
		for (Trigger trigger : eNode.getTriggers()) {
			if (trigger.type() == TYPE.DEFAULT_ERROR)
				defaultError = trigger;
			else
			if (trigger.type() == TYPE.ERROR) {
				if (t instanceof TaskException){
					if (trigger.name().equals(((TaskException)t).getTrigger()))
						errorHandler = trigger;
				}
			}
		}
		if (errorHandler == null) errorHandler = defaultError;
		if (errorHandler != null) {
			// create new activity
			EElement start = context.getEPool().getElement(errorHandler.activity().getCanonicalName());
			try {
				createActivity(context, pNode, start);
				// close node
				closeFlowNode(context,pNode,STATE_NODE.CLOSED);
				return;
			} catch (Exception e) {
				log().e(e);
				fireEvent.error(pNode,start,e);
			}
		} else {
			// set node in error
			pNode.setState(STATE_NODE.FAILED);
			pNode.setExitMessage(t.toString());
			try {
				saveFlowNode(pNode);
			} catch (NotFoundException | IOException e) {
				log().e(pNode,e);
			}
		}
		
	}

	public void closeFlowNode(EngineContext context, PNode pNode, STATE_NODE state) throws IOException {
		fireEvent.closeFlowNode(pNode, state);
		
		if (context != null)
			fireEvent.closedActivity(context.getARuntime(), pNode);
		
		if (context != null && context.getPCase() != null) {
			synchronized (context.getLock()) {
				pNode.setState(state);
				synchronized (nodeCache) {
					storage.saveFlowNode(pNode);
					nodeCache.put(pNode.getId(), pNode);
				}
				// savePCase(context);
			}
		} else {
			pNode.setState(state);
			synchronized (nodeCache) {
				storage.saveFlowNode(pNode);
				nodeCache.put(pNode.getId(), pNode);
			}
		}
	}
	
	public PCase getCase(UUID caseId) throws NotFoundException, IOException {
		synchronized (caseCache) {
			PCase caze = caseCache.get(caseId);
			if (caze == null) {
				caze = storage.loadCase(caseId);
				caseCache.put(caseId, caze);
			}
			return caze;
		}
	}

	@Override
	public PNode getFlowNode(UUID nodeId) throws NotFoundException, IOException {
		synchronized (nodeCache) {
			PNode node = nodeCache.get(nodeId);
			if (node == null) {
				node = storage.loadFlowNode(nodeId);
				nodeCache.put(nodeId, node);
			}
			return node;
		}
	}
	
// ---
	
	public Object doExecute(String uri) throws Exception {
		MUri u = MUri.toUri(uri);
		return execute(u);
	}
	
	public Object execute(MUri uri) throws Exception {
		return execute(uri, null);
	}
	
	/**
	 * Execute the URI command. 
	 * bpm:// - start case
	 * bpmm:// - send message
	 * bpms:// - send signal
	 * bpme:// - send external
	 * bpmx:// - execute additional start point
	 * 
	 * @param uri
	 * @param parameters if null the parameters are taken from uri query
	 * @return The result of the action, e.g. UUID for new case.
	 * @throws Exception
	 */
	@Override
	public Object execute(MUri uri, IProperties parameters) throws Exception {
		switch (uri.getScheme()) {
		case "bpm": {
			// check access
			String user = uri.getUsername();
			if (user != null) {
				String pass = uri.getPassword();
				if (!config.aaa.validatePassword(user, pass))
					throw new AccessDeniedException("login failed",user,uri);

				if (!hasInitiateAccess(uri, user))
					throw new AccessDeniedException("user is not initiator",user,uri);
			}
			UUID id = (UUID)start(uri, null, parameters);
			
			String[] uriParams = uri.getParams();
			if (uriParams != null && uriParams.length > 0) {
				MProperties options = MProperties.explodeToMProperties(uriParams);
				if (options.getBoolean( EngineConst.PARAM_PROGRESS, false)) {
					long waitTime = 300;
					long start = System.currentTimeMillis();
					long timeout = MCast.tolong(config.persistent.getParameters().get(EngineConst.ENGINE_PROGRESS_TIMEOUT), MTimeInterval.MINUTE_IN_MILLISECOUNDS * 5);
					while (true) {
						PCase caze = getCase(id);
						if (
								EngineConst.MILESTONE_PROGRESS.equals(caze.getMilestone())) {
							break;
						}
						if (	caze.getState() == STATE_CASE.CLOSED || 
								caze.getState() == STATE_CASE.SUSPENDED
							)
							throw new MException("Progress not reached before close",id);
						if (MTimeInterval.isTimeOut(start, timeout)) 
							throw new TimeoutRuntimeException("Wait for progress timeout",id);
						Thread.sleep(waitTime);
					}
				}
			}
			return id;
		}
		case "bmpm": {
			// check access
			String user = uri.getUsername();
			if (user != null) {
				String pass = uri.getPassword();
				if (!config.aaa.validatePassword(user, pass))
					throw new AccessDeniedException("login failed",user,uri);

				if (!hasInitiateAccess(uri, user))
					throw new AccessDeniedException("user is not initiator",user,uri);
			}
			
			UUID caseId = null;
			String l = uri.getLocation();
			if (MValidator.isUUID(l)) caseId = UUID.fromString(l);
			String m = uri.getPath();

			if (parameters == null) {
				parameters = new MProperties();
				Map<String, String> p = uri.getQuery();
				if (p != null) parameters.putAll(p);
			}
			fireMessage(caseId, m, parameters);
			return null;
		}
		case "bpms": {
			// check access
			String user = uri.getUsername();
			if (user != null) {
				String pass = uri.getPassword();
				if (!config.aaa.validatePassword(user, pass))
					throw new AccessDeniedException("login failed",user,uri);

				if (!hasInitiateAccess(uri, user))
					throw new AccessDeniedException("user is not initiator",user,uri);
			}

			String signal = uri.getPath();
			if (parameters == null) {
				parameters = new MProperties();
				Map<String, String> p = uri.getQuery();
				if (p != null) parameters.putAll(p);
			}
			return fireSignal(signal, parameters);
		}
		case "bpme": {
			String l = uri.getLocation();
			if (!MValidator.isUUID(l)) throw new MException("misspelled node id",l);
			UUID nodeId = UUID.fromString(l);

			String user = uri.getUsername();
			if (user != null) {
				String pass = uri.getPassword();
				if (!config.aaa.validatePassword(user, pass))
					throw new AccessDeniedException("login failed",user,uri);
				
				if (!hasExecuteAccess(nodeId, user))
					throw new AccessDeniedException("user can't execute",user,uri);
			}
			
			if (parameters == null) {
				parameters = new MProperties();
				Map<String, String> p = uri.getQuery();
				if (p != null) parameters.putAll(p);
			}
			fireExternal(nodeId, parameters);
			return null;
		}
		case "bpmx": {
			String l = uri.getLocation();
			if (!MValidator.isUUID(l)) throw new MException("misspelled case id",l);
			UUID caseId = UUID.fromString(l);
			// check start point
			PCase caze = getCase(caseId);
			if (caze == null) 
				throw new MException("case not found",caseId);
			if (caze.getState() == STATE_CASE.SUSPENDED)
				throw new MException("case suspended",caseId);
			if (caze.getState() == STATE_CASE.CLOSED)
				throw new MException("case closed",caseId);
			// check access
			String user = uri.getUsername();
			if (user != null) {
				String pass = uri.getPassword();
				if (!config.aaa.validatePassword(user, pass))
					throw new AccessDeniedException("login failed",user,uri);
				
				if (!hasInitiateAccess(uri, user))
					throw new AccessDeniedException("user is not initiator",user,uri);
			}
			// parameters
			if (parameters == null) {
				parameters = new MProperties();
				Map<String, String> p = uri.getQuery();
				if (p != null) parameters.putAll(p);
			}
			// context and start
			EngineContext context = createContext(caze);
			EElement start = context.getEPool().getElement(uri.getFragment());
			if (start == null)
				throw new MException("start point not found",uri.getFragment());
			
			return createStartPoint(context, start);
		}
		// case "bpmq": // not implemented use executeQuery()
		default:
			throw new MException("scheme unknown",uri.getScheme());
		}
	}
	
	public UUID start(String uri) throws Exception {
		MUri u = MUri.toUri(uri);
		Map<String, String> q = u.getQuery();
		MProperties properties = new MProperties();
		if (q != null)
			properties.putAll(q);
		return start(u, null, properties);
	}
	
	public UUID start(String uri, IProperties properties) throws Exception {
		MUri u = MUri.toUri(uri);
		return start(u, null, properties);
	}
	
	public UUID start(MUri uri, UUID closeActivity, IProperties properties) throws Exception {
		if (!EngineConst.SCHEME_REACTIVE.equals(uri.getScheme()))
			throw new UsageException("unknown uri scheme",uri,"should be",EngineConst.SCHEME_REACTIVE);
		
		if (properties == null) {
			properties = new MProperties();
		}
		Map<String, String> query = uri.getQuery();
		if (query != null)
			properties.putAll(query);

		// get process
		EProcess process = getProcess(uri);
		
		if (process == null) throw new NotFoundException("process unknown",uri);
				
		// load pool
		EPool pool = getPool(process, uri);
		
		if (pool == null)
			throw new NotFoundException("pool not found in process",uri);
		
		// remember options
		String[] uriParams = uri.getParams();
		MProperties options = null;
		if (uriParams != null && uriParams.length > 0) {
			options = MProperties.explodeToMProperties(uriParams);
		} else {
			options = new MProperties();
		}

		// update uri
		MutableUri u = new MutableUri(uri.toString());
		u.setLocation(process.getCanonicalName() + ":" + process.getVersion() );
		u.setPath(pool.getCanonicalName());
		u.setQuery(null);
		u.setParams(null);
		MUri originalUri = uri;
		uri = u;
		
		// load start points
		List<EElement> startPoints = null;
		String fragment = uri.getFragment();
		if (fragment != null) {
			EElement point = pool.getElement(fragment);
			if (point == null)
				throw new MException("start point not found",fragment,uri);
			if (!point.is(AStartPoint.class))
				throw new MException("node is not a start point",uri);
			startPoints = new LinkedList<>();
			startPoints.add(point);
		} else {
			startPoints = pool.getStartPoints();
			// remove inactive startpoints from list
			startPoints.removeIf(s -> s.isInterface(InactiveStartPoint.class));
		}
		
		if (startPoints.size() == 0)
			throw new NotFoundException("no start point found",uri);
				
		String createdBy = config.aaa.getCurrentUserId();
		
		// everything fine ... start creating
		fireEvent.startCase(originalUri,uri, properties, process, pool, startPoints, options, createdBy);

		// the context
		EngineContext context = new EngineContext(this);
		context.setUri(uri.toString());
		context.setEProcess(process);
		context.setEPool(pool);
		
		// ID could be defined in the options, must be a uuid and unique
		UUID id = null;
		Object uuid = options.get(EngineConst.OPTION_UUID);
		if (uuid != null) {
			id = UUID.fromString(uuid.toString());
			// check if exists
			try {
				if (storage.loadCase(id) != null)
					throw new MException("case already exists with uuid",id);
			} catch (NotFoundException e) {
				// everything is fine
			}
		} else
			id = UUID.randomUUID();
		
		if (closeActivity == null && options.isProperty(EngineConst.OPTION_CLOSE_ACTIVITY)) {
			closeActivity = UUID.fromString(options.getString(EngineConst.OPTION_CLOSE_ACTIVITY, null));
		}
		
		// create the PCase
		PCase pCase = new PCase(
				id,
				options, 
				uri.toString(), 
				pool.getName(),
				pool.getCanonicalName(), 
				System.currentTimeMillis(),
				createdBy,
				STATE_CASE.NEW, 
				0,
				closeActivity,
				properties,
				EngineConst.MILESTONE_START
			);
		context.setPCase(pCase);
		
		// create the APool
		APool<?> aPool = createPoolObject(pool);
		context.setAPool(aPool);
		
		// life cycle case pool
		if (aPool instanceof ContextRecipient)
			((ContextRecipient)aPool).setContext(context);
		aPool.initializeCase(properties);
		pCase.getParameters().clear(); // cleanup before first save, will remove parameters from external input
		savePCase(pCase, aPool, true);
		synchronized (getCaseLock(pCase)) {
			// create start point flow nodes
			Throwable isError = null;
			for (EElement start : startPoints) {
				try {
					createStartPoint(context, start);
				} catch (Throwable t) {
					log().w(start,t);
					fireEvent.error(pCase,start,t);
					isError = t;
					break;
				}
			}
			if (isError != null) {
				storage.deleteCaseAndFlowNodes(pCase.getId());
				throw new Exception(isError);
			}
			
			pCase.setState(STATE_CASE.RUNNING);
			savePCase(pCase,aPool, false);
		}
		return pCase.getId();
	}

	public void savePCase(EngineContext context) throws IOException {
		savePCase(context.getPCase(), context.getPool(), false);
	}
	
	public void savePCase(PCase pCase, APool<?> aPool, boolean init) throws IOException {
		Map<String, Object> newParameters = aPool.exportParamters();
		pCase.getParameters().putAll(newParameters);
		
		if (aPool instanceof IndexValuesProvider) {
			pCase.setIndexValues( ((IndexValuesProvider)aPool).createIndexValues(init) );
		} else {
			pCase.setIndexValues(null);
		}
		
		fireEvent.saveCase(pCase, aPool);
		synchronized (getCaseLock(pCase)) {
			synchronized (caseCache) {
				caseCache.put(pCase.getId(), pCase);
				storage.saveCase(pCase);
			}
		}
	}

	public EPool getPool(EProcess process, MUri uri) throws NotFoundException {
		String poolName = uri.getPath();
		if (MString.isEmpty(poolName))
			poolName = process.getProcessDescription().defaultPool();
		if (MString.isEmpty(poolName))
			throw new NotFoundException("default pool not found for process",uri);
		
		EPool pool = process.getPool(poolName);
		return pool;
	}

	public EProcess getProcess(MUri uri) throws MException {
		// load process
		String processName = uri.getLocation();
		String processVersion = null;
		if (MString.isIndex(processName, ':')) {
			processVersion = MString.afterIndex(processName, ':');
			processName = MString.beforeIndex(processName, ':');
		}
		if (processVersion == null)
			processVersion = config.persistent.getActiveProcessVersion(processName);
		else {
			if (!config.persistent.isProcessEnabled(processName, processVersion))
				throw new MException("specified process version is not enabled",processName, processVersion,uri);
		}
		if (MString.isEmpty(processVersion))
			throw new MException("default process version is disabled",processName,uri);
		
		EProcess process = processProvider.getProcess(processName, processVersion);
		return process;
	}

	public UUID createStartPoint(EngineContext context, EElement start) throws Exception {
		
		// some checks
		if (!start.is(AStartPoint.class))
			throw new MException("activity is not a start point",context,start);
		
		if (!context.getEPool().isElementOfPool(start))
			throw new MException("start point is not part of the pool",context,start);

		// collect information
		ActivityDescription desc = start.getActivityDescription();
		Class<? extends RuntimeNode> runtimeClass = desc.runtime();
		
		UUID caseId = context.getPCase().getId();
		fireEvent.createStartPoint(context.getPCase(),start);
		
		Class<? extends AActor> actor = start.getAssignedActor(context.getEPool());
		
		// create runtime
		PNode runtime = new PNode(
				UUID.randomUUID(), 
				caseId,
				"runtime",
				runtimeClass.getCanonicalName(),
				System.currentTimeMillis(),
				0,
				STATE_NODE.WAITING,
				STATE_NODE.WAITING,
				null,
				null,
				null,
				false,
				TYPE_NODE.RUNTIME,
				null,
				new HashMap<>(),
				null,
				null,
				0,
				null
				);
		fireEvent.createRuntime(context.getPCase(),start,runtime);
		synchronized (nodeCache) {
			storage.saveFlowNode(runtime);
			nodeCache.put(runtime.getId(), runtime);
		}
		context.setPRuntime(runtime);
		UUID runtimeId = runtime.getId();
		
		// create flow node
		PNode flow = new PNode(
				UUID.randomUUID(), 
				caseId, 
				start.getName(),
				start.getCanonicalName(),
				System.currentTimeMillis(),
				0,
				STATE_NODE.NEW,
				STATE_NODE.NEW, 
				start.getSchedulerList(),
				start.getSignalList(),
				start.getMessageList(),
				false, 
				TYPE_NODE.NODE, 
				null, 
				null, 
				null, 
				runtimeId,
				EngineConst.TRY_COUNT,
				actor == null ? null : actor.getName()
			);
		flow.setScheduledNow();
		fireEvent.createStartNode(context.getARuntime(), flow, context.getPCase(),start);
		context = new EngineContext(context, flow);
		synchronized (context.getLock()) {
			doNodeLifecycle(context, flow);
		}
		return flow.getId();
	}
	
	public PNode createActivity(EngineContext context, PNode previous, EElement start) throws Exception {
		
		UUID caseId = context.getPCase().getId();
		UUID runtimeId = previous.getRuntimeId();
		
		Class<? extends AActor> actor = start.getAssignedActor(context.getEPool());

		// create flow node
		PNode flow = new PNode(
				UUID.randomUUID(), 
				caseId, 
				start.getName(),
				start.getCanonicalName(),
				System.currentTimeMillis(),
				0,
				STATE_NODE.NEW,
				STATE_NODE.NEW, 
				start.getSchedulerList(),
				start.getSignalList(),
				start.getMessageList(),
				false, 
				TYPE_NODE.NODE, 
				null, 
				null, 
				null, 
				runtimeId,
				EngineConst.TRY_COUNT,
				actor == null ? null : actor.getName()
			);
		//flow.setScheduledNow();
		fireEvent.createActivity(context.getARuntime(), flow, context.getPCase(),previous,start);
		context = new EngineContext(context, flow);
		
		synchronized (context.getLock()) {
			doNodeLifecycle(context, flow);
		}
		
		return flow;
	}
	
	protected void doNodeLifecycle(EngineContext context, PNode flow) throws Exception {
		
		boolean init = flow.getStartState() == STATE_NODE.NEW; // this means the node is not executed!
		context = new EngineContext(context, flow);
		
		// lifecycle flow node
		EElement start = context.getENode();
		AActivity<?> activity = (AActivity<?>) createActivityObject(start);
		context.setANode(activity);
		RuntimeNode runtime = context.getARuntime();
		if (init)
			fireEvent.initStart(runtime,flow,start,activity);
		else
			fireEvent.executeStart(runtime,flow,start,activity);
		if (activity instanceof ContextRecipient)
			((ContextRecipient)activity).setContext(context);
		activity.importParameters(flow.getParameters());
		
		try {
			if (init) {
				activity.initializeActivity();

				if (activity instanceof IndexValuesProvider) {
					flow.setIndexValues( ((IndexValuesProvider)activity).createIndexValues(true) );
				} else {
					flow.setIndexValues(null);
				}

			} else {
				flow.setLastRunDate(System.currentTimeMillis());
				activity.doExecuteActivity();
			}
			// secure switch state away from NEW
			if (flow.getState() == STATE_NODE.NEW) {
				flow.setState(STATE_NODE.RUNNING);
				flow.setScheduledNow();
			} else
			if (flow.getStartState() == STATE_NODE.RUNNING && flow.getState() == STATE_NODE.RUNNING) {
				flow.setTryCount(flow.getTryCount()-1);
				if (flow.getTryCount() <= 0) {
					flow.setSuspendedState(STATE_NODE.RUNNING);
					flow.setState(STATE_NODE.STOPPED);
				} else {
					flow.setScheduled(newScheduledTime(flow));
				}
			}
		} catch (Throwable t) {
			log().w(flow,t);
			// remember
			fireEvent.error(flow,t);
			if (init)
				fireEvent.initFailed(runtime,flow);
			else
				fireEvent.executeFailed(runtime,flow);
			doNodeErrorHandling(context, flow, t);
			return;
		}
		if (init)
			flow.getParameters().clear();
		
		// save
		saveFlowNode(context, flow,activity);
		
		if (init)
			fireEvent.initStop(runtime,flow);
		else
			fireEvent.executeStop(runtime,flow);
	}
	
	private long newScheduledTime(PNode flow) {
		return System.currentTimeMillis() + MTimeInterval.MINUTE_IN_MILLISECOUNDS;
	}

	public APool<?> createPoolObject(EPool pool) throws InstantiationException, IllegalAccessException {
		return pool.getPoolClass().newInstance();		
	}

	public AElement<?> createActivityObject(EElement element) throws InstantiationException, IllegalAccessException {
		return element.getElementClass().newInstance();		
	}

	/**
	 * Create a runtime node from P-Object.
	 * 
	 * @param context
	 * @param runtime
	 * @return Runtime object
	 */
	public RuntimeNode createRuntimeObject(EngineContext context, PNode runtime) {
		String canonicalName = runtime.getCanonicalName();
		RuntimeNode out = null;
		if (RuntimeNode.class.getCanonicalName().equals(canonicalName))
			out = new RuntimeNode();
		else
			out = (RuntimeNode)context.getEPool().getElement(canonicalName);
		// lifecycle
		if (out instanceof ContextRecipient)
			((ContextRecipient)out).setContext(context);
		
		out.importParameters(runtime.getParameters());
		
		return out;
	}

	/**
	 * Load the runtime P-Object from storage.
	 * 
	 * @param context
	 * @param pNode
	 * @return The runtime persistent object
	 */
	public PNode getRuntimeForPNode(EngineContext context, PNode pNode) {
		if (pNode == null || pNode.getRuntimeId() == null) return null;
		try {
			return getFlowNode(pNode.getRuntimeId());
		} catch (NotFoundException | IOException e) {
			log().w(pNode,e);
			return null;
		}
	}

	/**
	 * Return the object of the swim lane.
	 * 
	 * @param context
	 * @param eNode
	 * @return The Swim lane object
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public ASwimlane<?> createSwimlaneObject(EngineContext context, EElement eNode) throws InstantiationException, IllegalAccessException {
		ASwimlane<?> out = eNode.getSwimlane().newInstance();
		// lifecycle
		if (out instanceof ContextRecipient)
			((ContextRecipient)out).setContext(context);
		return out;
	}

	/**
	 * Save the runtime to storage. If runtme object is given, the
	 * parameters will be stored.
	 * 
	 * @param pRuntime
	 * @param aRuntime
	 * @throws IOException
	 */
	public void saveRuntime(PNode pRuntime, RuntimeNode aRuntime) throws IOException {
		if (aRuntime != null) {
			Map<String, Object> parameters = aRuntime.exportParamters();
			if (parameters != null) {
				pRuntime.getParameters().putAll(parameters);
			}
		}
		fireEvent.saveRuntime(pRuntime, aRuntime);
		synchronized (nodeCache) {
			storage.saveFlowNode(pRuntime);
			nodeCache.put(pRuntime.getId(), pRuntime);
		}
	}

	/**
	 * Archive all closed cases.
	 * 
	 */
	public void archiveAll() {
		try {
			archive.saveEngine(config.persistent);
			for (PCaseInfo caseId : storage.getCases(STATE_CASE.CLOSED)) {
				PCase caze = getCase(caseId.getId());
				synchronized (getCaseLock(caze)) {
					fireEvent.archiveCase(caze);
					archive.saveCase(caze);
					for (PNodeInfo nodeId : storage.getFlowNodes(caze.getId(), null)) {
						PNode node = getFlowNode(nodeId.getId());
						archive.saveFlowNode(node);
					}
					storage.deleteCaseAndFlowNodes(caze.getId());
				}
			}
		} catch (Throwable t) {
			log().e(t);
		}
	}

	/**
	 * This will archive the case and delete it in from storage.
	 * 
	 * @param caseId
	 * @throws IOException
	 * @throws MException 
	 */
	public void archiveCase(UUID caseId) throws IOException, MException {
		try {
			PCase caze = getCase(caseId);
			if (caze != null) {
				synchronized (getCaseLock(caze)) {
					fireEvent.archiveCase(caze);
					if (caze.getState() != STATE_CASE.CLOSED)
						throw new MException("case is not closed",caseId);
					archive.saveCase(caze);

					for (PNodeInfo nodeId : storage.getFlowNodes(caseId, null)) {
						try {
							PNode node = getFlowNode(nodeId.getId());
							archive.saveFlowNode(node);
						} catch (NotFoundException e) {
							log().d(caseId,e);
						}
					}
					
					storage.deleteCaseAndFlowNodes(caseId);
					return;
				}
			}
		} catch (NotFoundException e) {
			log().d(caseId,e);
		}
		for (PNodeInfo nodeId : storage.getFlowNodes(caseId, null)) {
			try {
				PNode node = getFlowNode(nodeId.getId());
				archive.saveFlowNode(node);
			} catch (NotFoundException e) {
				log().d(caseId,e);
			}
		}
		storage.deleteCaseAndFlowNodes(caseId);
	}

	/**
	 * Set a case and all nodes to suspended state.
	 * 
	 * @param caseId
	 * @throws IOException
	 * @throws MException
	 */
	public void suspendCase(UUID caseId) throws IOException, MException {
		PCase caze = getCase(caseId);
		synchronized (getCaseLock(caze)) {
			if (caze.getState() == STATE_CASE.SUSPENDED)
				throw new MException("case already suspended",caseId);
			fireEvent.suspendCase(caze);
			caze.setState(STATE_CASE.SUSPENDED);
			storage.saveCase(caze);
			for (PNodeInfo nodeId : storage.getFlowNodes(caseId, null)) {
				PNode node = getFlowNode(nodeId.getId());
				if (node.getState() != STATE_NODE.SUSPENDED && node.getState() != STATE_NODE.CLOSED) {
					node.setSuspendedState(node.getState());
					node.setState(STATE_NODE.SUSPENDED);
					storage.saveFlowNode(node);
				}
			}
		}
	}

	/**
	 * Return a suspended case and all nodes to 'normal' state.
	 * 
	 * @param caseId
	 * @throws IOException
	 * @throws MException
	 */
	public void resumeCase(UUID caseId) throws IOException, MException {
		PCase caze = getCase(caseId);
		synchronized (getCaseLock(caze)) {
			if (caze.getState() != STATE_CASE.SUSPENDED)
				throw new MException("already is not suspended",caseId);
			fireEvent.unsuspendCase(caze);
			caze.setState(STATE_CASE.RUNNING);
			storage.saveCase(caze);
			for (PNodeInfo nodeId : storage.getFlowNodes(caseId, null)) {
				PNode node = getFlowNode(nodeId.getId());
				if (node.getState() == STATE_NODE.SUSPENDED && node.getSuspendedState() != STATE_NODE.NEW) {
					node.setState(node.getSuspendedState());
					node.setSuspendedState(STATE_NODE.NEW);
					storage.saveFlowNode(node);
				}
			}
		}
	}
	
	/**
	 * Set flow node to stopped. Not possible for suspended nodes.
	 * 
	 * @param nodeId
	 * @throws MException
	 * @throws IOException
	 */
	public void cancelFlowNode(UUID nodeId) throws MException, IOException {
		PNode node = getFlowNode(nodeId);
		synchronized (getCaseLock(node)) {
			if (node.getStartState() == STATE_NODE.SUSPENDED)
				throw new MException("node is suspended",nodeId);
			fireEvent.cancelFlowNode(node);
			node.setSuspendedState(node.getState());
			node.setState(STATE_NODE.CLOSED);
			storage.saveFlowNode(node);
		}
	}
	
	/**
	 * Set a flow node to running state. Not possible for suspended nodes.
	 * @param nodeId
	 * @throws IOException
	 * @throws MException
	 */
	public void retryFlowNode(UUID nodeId) throws IOException, MException {
		PNode node = getFlowNode(nodeId);
		synchronized (getCaseLock(node)) {
			if (node.getStartState() == STATE_NODE.SUSPENDED)
				throw new MException("node is suspended",nodeId);
			fireEvent.retryFlowNode(node);
			node.setSuspendedState(node.getState());
			node.setState(STATE_NODE.RUNNING);
			node.setScheduledNow();
			storage.saveFlowNode(node);
		}
	}
	
	/**
	 * Migrate the content of a running case. The case must be suspended or closed.
	 * The case will be archived before migration. It is possible to restore the case
	 * after fail.
	 * 
	 * @param caseId
	 * @param migrator
	 * @param toUri 
	 * @throws MException
	 * @throws IOException
	 */
	public void migrateCase(UUID caseId, String toUri, String migrator) throws MException, IOException {
		PCase caze = getCase(caseId);
		if (caze.getState() != STATE_CASE.SUSPENDED && caze.getState() != STATE_CASE.CLOSED)
			throw new MException("already is not suspended",caseId);
		
		fireEvent.migrateCase(caze, toUri, migrator);
		
		// create from context
		EngineContext fromContext = createContext(caze);
		
		// create to context
		MUri uri = MUri.toUri(toUri);
		EProcess process = getProcess(uri);
		EPool pool = getPool(process, uri);
		
		EngineContext toContext = new EngineContext(this);
		toContext.setUri(uri.toString());
		toContext.setEProcess(process);
		toContext.setEPool(pool);
		
		// create migrator
		Migrator migratorObj = null;
		for (Class<? extends Migrator> ano : process.getProcessDescription().migrator())
				if (ano.getName().equals(migrator)) {
					try {
						migratorObj = ano.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new MException(toUri,migrator,e);
					}
				}
		if (migratorObj == null) {
			try {
				migratorObj = (Migrator) this.getClass().getClassLoader().loadClass(migrator).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			}
		}
		if (migratorObj == null)
			throw new MException("migrator not found",migrator);
		
		// load all nodes
		LinkedList<PNode> nodes = new LinkedList<>();
		for (PNodeInfo nodeId : storage.getFlowNodes(caseId, null)) {
			PNode node = getFlowNode(nodeId.getId());
			nodes.add(node);
		}
		
		synchronized (getCaseLock(caze)) {
			// archive the case state
			archive.saveCase(caze);
			for (PNode node : nodes)
				archive.saveFlowNode(node);
	
			// migrate
			migratorObj.doMigrate(new EngineMigrateContext(fromContext, toContext, caze, nodes));
			
			// validate output
			if (caze.getState() != STATE_CASE.SUSPENDED && caze.getState() != STATE_CASE.CLOSED)
				throw new MException("It's not allowed to change case state, change suspendedState instead");
			for (PNode node : nodes)
				if (node.getState() != STATE_NODE.SUSPENDED && node.getState() != STATE_NODE.CLOSED)
					throw new MException("It's not allowed to change flow node state, change suspendedState instead");
			
			// store
			storage.saveCase(caze);
			for (PNode node : nodes)
				storage.saveFlowNode(node);
		}
	}
	
	/**
	 * Copy the case from the archive to the storage. Only suspended, closed
	 * or deleted cases can be restored. The restored case will be in suspended state.
	 * 
	 * @param caseId
	 * @throws IOException
	 * @throws MException
	 */
	public void restoreCase(UUID caseId) throws IOException, MException {

		try {
			PCase caze = getCase(caseId);
			if (caze != null) {
				fireEvent.archiveCase(caze);
				if (caze.getState() != STATE_CASE.CLOSED && caze.getState() != STATE_CASE.SUSPENDED)
					throw new MException("case is not closed or suspended",caseId);
			}
		} catch (NotFoundException e) {
			log().d(caseId,e);
		}
		PCase caze = archive.loadCase(caseId);
		fireEvent.restoreCase(caze);
		synchronized (getCaseLock(caze)) {
			caze.setState(STATE_CASE.SUSPENDED);
			storage.saveCase(caze);
			caze = getCase(caseId);
			for (PNodeInfo nodeId : archive.getFlowNodes(caseId, null)) {
				PNode node = getFlowNode(nodeId.getId());
				// set to suspended
				if (node.getState() != STATE_NODE.CLOSED && node.getState() != STATE_NODE.SUSPENDED) {
					node.setSuspendedState(node.getState());
					node.setState(STATE_NODE.SUSPENDED);
				}
				storage.saveFlowNode(node);
			}
		}
	}
	
	// -- aaa provider
	
	public AaaProvider getAaaProvider() {
		return config.aaa;
	}

	// -- storage
	
	@Override
	public Result<PCaseInfo> storageSearchCases(SearchCriterias criterias) throws IOException {
		return storage.searchCases(criterias);
	}
	
	@Override
	public Result<PCaseInfo> storageGetCases(STATE_CASE state) throws IOException {
		return storage.getCases(state);
	}

	@Override
	public Result<PNodeInfo> storageGetFlowNodes(UUID caseId, STATE_NODE state) throws IOException {
		return storage.getFlowNodes(caseId, state);
	}

	@Override
	public Result<PNodeInfo> storageSearchFlowNodes(SearchCriterias criterias) throws IOException {
		return storage.searchFlowNodes(criterias);
	}
	
	@Override
	public Result<PNodeInfo> storageGetScheduledFlowNodes(STATE_NODE state, long scheduled) throws IOException {
		return storage.getScheduledFlowNodes(state, scheduled);
	}

	@Override
	public Result<PNodeInfo> storageGetSignaledFlowNodes(STATE_NODE state, String signal) throws IOException {
		return storage.getSignalFlowNodes(state, signal);
	}

	@Override
	public Result<PNodeInfo> storageGetMessageFlowNodes(UUID caseId, STATE_NODE state, String message) throws IOException {
		return storage.getMessageFlowNodes(caseId, state, message);
	}

	// -- archive

	public PCase archiveLoadCase(UUID id) throws IOException, NotFoundException {
		return archive.loadCase(id);
	}

	public PNode archiveLoadFlowNode(UUID id) throws IOException, NotFoundException {
		return archive.loadFlowNode(id);
	}

	public Result<PCaseInfo> archiveGetCases(STATE_CASE state) throws IOException {
		return archive.getCases(state);
	}

	public Result<PNodeInfo> archiveGetFlowNodes(UUID caseId, STATE_NODE state) throws IOException {
		return archive.getFlowNodes(caseId, state);
	}

	public Result<PNodeInfo> archiveGetScheduledFlowNodes(STATE_NODE state, long scheduled) throws IOException {
		return archive.getScheduledFlowNodes(state, scheduled);
	}

	public Result<PNodeInfo> archiveGetSignaledFlowNodes(STATE_NODE state, String signal) throws IOException {
		return archive.getSignalFlowNodes(state, signal);
	}

	public Result<PNodeInfo> archiveGetMessageFlowNodes(UUID caseId, STATE_NODE state, String message) throws IOException {
		return archive.getMessageFlowNodes(caseId, state, message);
	}

	// --
	
	public EngineContext createContext(PCase caze) throws MException {
		
		MUri uri = MUri.toUri(caze.getUri());
		EProcess process = getProcess(uri);
		EPool pool = getPool(process, uri);
		
		EngineContext context = new EngineContext(this);
		context.setUri(uri.toString());
		context.setEProcess(process);
		context.setEPool(pool);
		context.setPCase(caze);
		return context;

	}

	public EngineContext createContext(PCase caze, PNode pNode) throws MException {
		
		MUri uri = MUri.toUri(caze.getUri());
		EProcess process = getProcess(uri);
		EPool pool = getPool(process, uri);
		
		EngineContext context = new EngineContext(this, pNode);
		context.setUri(uri.toString());
		context.setEProcess(process);
		context.setEPool(pool);
		context.setPCase(caze);
		return context;
	}

	public void doCloseActivity(ProcessContext<?> closedContext, UUID nodeId) throws IOException, MException {
		PNode node = getFlowNode(nodeId);
		PCase caze = getCase(node.getCaseId());
		
		if (node.getState() != STATE_NODE.WAITING && node.getState() != STATE_NODE.SCHEDULED) {
			closedContext.getARuntime().doErrorMsg(node, "call back node is no more running");
			return;
		}
		
		if (caze.getState() != STATE_CASE.RUNNING) {
			closedContext.getARuntime().doErrorMsg(node, "call back case is no more running");
			return;
		}
		
		synchronized (getCaseLock(caze)) {
			EngineContext context = createContext(caze, node);
			AElement<?> aNode = context.getANode();
			try {
				((CloseActivity)aNode).doClose(closedContext);
			} catch (Exception e) {
				doNodeErrorHandling(context, node, e);
				log().e("doCloseActivity",nodeId,e);
			}
			savePCase(context);
		}
	}

	public void saveEnginePersistence() {
		try {
			storage.saveEngine(config.persistent);
		} catch (IOException e) {
			log().e(e);
		}
	}

	public void loadEnginePersistence() {
		try {
			config.persistent = storage.loadEngine();
		} catch (NotFoundException | IOException e) {
			log().e(e);
		}
	}

	public boolean hasReadAccess(String uri, String user) {
		if (!config.aaa.isUserActive(user)) return false;
		MUri muri = MUri.toUri(uri);
		try {
			EProcess process = getProcess(muri);
			if (process == null) return false;
			EPool pool = getPool(process, muri);
			if (pool == null) return false;
			EngineContext context = new EngineContext(this);
			context.setUri(uri);
			context.setEProcess(process);
			context.setEPool(pool);
			
			{
				Class<? extends AActor>[] actorClasss = pool.getPoolDescription().actorRead();
				for (Class<? extends AActor> actorClass : actorClasss) {
					AActor actor = actorClass.newInstance();
					if (actor instanceof ContextRecipient)
						((ContextRecipient)actor).setContext(context);
					boolean hasAccess = actor.hasAccess(user);
					if (hasAccess) return true;
				}
			}
			{
				Class<? extends AActor>[] actorClasss = pool.getPoolDescription().actorWrite();
				for (Class<? extends AActor> actorClass : actorClasss) {
					AActor actor = actorClass.newInstance();
					if (actor instanceof ContextRecipient)
						((ContextRecipient)actor).setContext(context);
					boolean hasAccess = actor.hasAccess(user);
					if (hasAccess) return true;
				}
			}
			
			return false;
			
		} catch (Throwable t) {
			log().e(uri,user,t);
			return false;
		}

	}

	public boolean hasWriteAccess(String uri, String user) {
		if (!config.aaa.isUserActive(user)) return false;
		
		MUri muri = MUri.toUri(uri);
		try {
			EProcess process = getProcess(muri);
			if (process == null) return false;
			EPool pool = getPool(process, muri);
			if (pool == null) return false;
			EngineContext context = new EngineContext(this);
			context.setUri(uri);
			context.setEProcess(process);
			context.setEPool(pool);
			
			{
				Class<? extends AActor>[] actorClasss = pool.getPoolDescription().actorWrite();
				for (Class<? extends AActor> actorClass : actorClasss) {
					AActor actor = actorClass.newInstance();
					if (actor instanceof ContextRecipient)
						((ContextRecipient)actor).setContext(context);
					boolean hasAccess = actor.hasAccess(user);
					if (hasAccess) return true;
				}
			}
			
			return false;
			
		} catch (Throwable t) {
			log().e(uri,user,t);
			return false;
		}

	}

	public boolean hasInitiateAccess(MUri uri, String user) {
		if (!config.aaa.isUserActive(user)) return false;
		try {

			EProcess process = getProcess(uri);
			if (process == null) return false;
			EPool pool = getPool(process, uri);
			if (pool == null) return false;
			EngineContext context = new EngineContext(this);
			context.setUri(uri.toString());
			context.setEProcess(process);
			context.setEPool(pool);
			Class<? extends AActor>[] actorClasss = pool.getPoolDescription().actorInitiator();
			for (Class<? extends AActor> actorClass : actorClasss) {
				AActor actor = actorClass.newInstance();
				if (actor instanceof ContextRecipient)
					((ContextRecipient)actor).setContext(context);
				boolean hasAccess = actor.hasAccess(user);
				if (hasAccess) return true;
			}
			return false;

		} catch (Throwable t) {
			log().e(uri,user,t);
			return false;
		}
	
	}
	
	public boolean hasExecuteAccess(UUID nodeId, String user) {
		if (!config.aaa.isUserActive(user)) return false;
		if (config.aaa.hasAdminAccess(user)) return true;
		
		try {
			// find actor
			PNode node = getFlowNode(nodeId);
			PCase caze = getCase(node.getCaseId());
			String uri = caze.getUri();
			
			MUri muri = MUri.toUri(uri);
			EProcess process = getProcess(muri);
			if (process == null) return false;
			EPool pool = getPool(process, muri);
			if (pool == null) return false;
			EngineContext context = new EngineContext(this, node);
			context.setUri(uri);
			context.setEProcess(process);
			context.setEPool(pool);
			context.setPCase(caze);
			EElement eNode = context.getENode();
			Class<? extends AActor> actorClass = eNode.getAssignedActor(pool);
			
			// create actor and let check access
			AActor actor = actorClass.newInstance();
			if (actor instanceof ContextRecipient)
				((ContextRecipient)actor).setContext(context);
			boolean hasAccess = actor.hasAccess(user);
			
			return hasAccess;
	
		} catch (Throwable t) {
			log().e(nodeId,user,t);
			return false;
		}

	}

	public void fireExternal(UUID nodeId, Map<String, Object> parameters) throws NotFoundException, IOException {
		fireEvent.fireExternal(nodeId,parameters);
		PNode node = getFlowNode(nodeId);
		synchronized (getCaseLock(node)) {
			if (node.getType() != TYPE_NODE.EXTERN) throw new NotFoundException("not external",nodeId);
			if (node.getState() == STATE_NODE.SUSPENDED) {
				if (node.getSuspendedState() != STATE_NODE.WAITING) throw new NotFoundException("not waiting",nodeId);
				node.setSuspendedState(STATE_NODE.RUNNING);
			} else {
				if (node.getState() != STATE_NODE.WAITING) throw new NotFoundException("not waiting",nodeId);
				node.setState(STATE_NODE.RUNNING);
				node.setScheduledNow();
			}
			node.setMessage(parameters);
			saveFlowNode(node);
		}
	}
	
	public void fireMessage(UUID caseId, String message, Map<String, Object> parameters) throws Exception {
		
		fireEvent.fireMessage(caseId,message,parameters);
		Result<PNodeInfo> res = storage.getMessageFlowNodes(caseId, PNode.STATE_NODE.WAITING, message);
		for (PNodeInfo nodeInfo : res ) {
			PNode node = getFlowNode(nodeInfo.getId());
			synchronized (getCaseLock(node)) {
				if (node.getState() == STATE_NODE.SUSPENDED) {
					log().w("message for suspended node will not be delivered",node,message);
					continue;
				} else 
				if (isExecuting(nodeInfo.getId())) {
					// to late ... 
					continue;
				}
				
				// is task listening ? check trigger list for ""
				String taskEvent = node.getMessageTriggers().get("");
				if (taskEvent != null && taskEvent.equals(message) && node.getState() == STATE_NODE.WAITING && node.getType() == TYPE_NODE.MESSAGE) {
					node.setState(STATE_NODE.RUNNING);
					node.setScheduledNow();
					node.setMessage(parameters);
					saveFlowNode(node);
					res.close();
					// message delivered ... bye
					return;
				}

				try {
					// find a trigger with the event
					PCase caze = getCase(caseId);
					EngineContext context = createContext(caze, node);
					for (Trigger trigger : ActivityUtil.getTriggers((AActivity<?>) context.getANode())) {
						if (trigger.type() == TYPE.MESSAGE && trigger.event().equals(message)) {
							// found one ... start new, close current
							PNode nextNode = createActivity(context, node, context.getEPool().getElement(trigger.activity().getCanonicalName()));
							nextNode.setMessage(parameters);
							saveFlowNode(context, nextNode, null);
							if (trigger.abord())
								closeFlowNode(context, node, STATE_NODE.CLOSED);
							res.close();
							return;
						}
					}
				} catch(Throwable e) {
					fireEvent.error(node,e);
					log().e(node,e);
					// should not happen, it's an internal engine problem
					try {
						PCase caze = getCase(node.getCaseId());
						EngineContext context = createContext(caze, node);
						closeFlowNode(context, node, STATE_NODE.SEVERE);
					} catch (Throwable e2) {
						log().e(nodeInfo,e2);
						fireEvent.error(nodeInfo,e2);
					}
					continue;
				}
			}
		
		} 
		throw new NotFoundException("node not found for message",caseId,message);
	}
	
	public int fireSignal(String signal, Map<String, Object> parameters) throws NotFoundException, IOException {
		
		fireEvent.fireSignal(signal,parameters);
		int cnt = 0;
		for (PNodeInfo nodeInfo : storage.getSignalFlowNodes(PNode.STATE_NODE.WAITING, signal)) {
			try {
				PNode node = getFlowNode(nodeInfo.getId());
				synchronized (getCaseLock(node)) {
					if (node.getState() == STATE_NODE.SUSPENDED) { // should not happen ... searching for WAITING nodes
						log().w("signal for suspended node will not be delivered",node,signal);
						continue;
					} else 
					if (isExecuting(nodeInfo.getId())) {
						// to late ... 
						continue;
					}
					// is task listening ? check trigger list for ""
					String taskEvent = node.getSignalTriggers().get("");
					if (taskEvent != null && taskEvent.equals(signal) && node.getState() == STATE_NODE.WAITING && node.getType() == TYPE_NODE.SIGNAL) {
						// trigger not found - its the message
						node.setState(STATE_NODE.RUNNING);
						node.setScheduledNow();
						node.setMessage(parameters);
						saveFlowNode(node);
						cnt++;
					} else {
						try {
							// find a trigger with the name
							PCase caze = getCase(node.getCaseId());
							EngineContext context = createContext(caze, node);
							for (Trigger trigger : ActivityUtil.getTriggers((AActivity<?>) context.getANode())) {
								if (trigger.type() == TYPE.SIGNAL && trigger.event().equals(signal)) {
									// found one ... start new, close current
									PNode nextNode = createActivity(context, node, context.getEPool().getElement(trigger.activity().getCanonicalName()));
									nextNode.setMessage(parameters);
									saveFlowNode(context, nextNode, null);
									if (trigger.abord())
										closeFlowNode(context, node, STATE_NODE.CLOSED);
									cnt++;
									continue;
								}
							}
						} catch(MException e) {
							fireEvent.error(node,e);
							log().e(node,e);
							// should not happen, it's an internal engine problem
							try {
								PCase caze = getCase(node.getCaseId());
								EngineContext context = createContext(caze, node);
								closeFlowNode(context, node, STATE_NODE.SEVERE);
							} catch (Throwable e2) {
								log().e(nodeInfo,e2);
								fireEvent.error(nodeInfo,e2);
							}
							continue;
						}
					}
					
				}
			} catch (Throwable t) {
				log().d(nodeInfo.getId(),t);
				// should not happen, it's an internal engine problem
				try {
					PNode node = getFlowNode(nodeInfo.getId());
					PCase caze = getCase(node.getCaseId());
					EngineContext context = createContext(caze, node);
					closeFlowNode(context, node, STATE_NODE.SEVERE);
				} catch (Throwable e) {
					log().e(nodeInfo,e);
					fireEvent.error(nodeInfo,e);
				}
			}
		}
		return cnt;
	}
	
	private void fireScheduledTrigger(PNodeInfo nodeInfo) {
		if (nodeInfo.getState() != STATE_NODE.WAITING) return;
		try {
			PNode node = getFlowNode(nodeInfo.getId());
			synchronized (getCaseLock(node)) {
				if (isExecuting(nodeInfo.getId())) {
					// to late ... 
					return;
				}
				Entry<String, Long> entry = node.getNextScheduled();
				if (entry == null) {
					// There is no need to be scheduled ....
					node.setScheduled(EngineConst.END_OF_DAYS);
					fireEvent.setScheduledToWaiting(node);
					saveFlowNode(node);
					return;
				}
				String triggerName = entry.getKey();
				if (triggerName.equals("")) {
					// for secure
					node.setScheduled(-1);
					saveFlowNode(node);
					return;
				}
				if (entry.getValue() > System.currentTimeMillis()) {
					// not reached ...
					node.setScheduled(entry.getValue());
					saveFlowNode(node);
					return;
				}
				// find trigger
				PCase caze = getCase(node.getCaseId());
				EngineContext context = createContext(caze, node);
				int cnt = 0;
				for (Trigger trigger : ActivityUtil.getTriggers((AActivity<?>) context.getANode())) {
					if (trigger.type() == TYPE.TIMER && (
							trigger.name().equals(triggerName)
							||
							triggerName.startsWith("trigger.") && cnt == MCast.toint(triggerName.substring(8), -1)
							)) {
						// found one ... start new, close current
						PNode nextNode = createActivity(context, node, context.getEPool().getElement(trigger.activity().getCanonicalName()));
						saveFlowNode(context, nextNode, null);
						if (trigger.abord())
							closeFlowNode(context, node, STATE_NODE.CLOSED);
						return;
					}
					cnt++;
				}
				// trigger not found
				fireEvent.error("Trigger for timer not found",triggerName,node);
				node.setSuspendedState(node.getState());
				node.setState(STATE_NODE.STOPPED);
				saveFlowNode(node);
			}
		} catch (Throwable t) {
			log().e(nodeInfo.getId(),t);
			// should not happen, it's an internal engine problem
			try {
				PNode node = getFlowNode(nodeInfo.getId());
				PCase caze = getCase(node.getCaseId());
				EngineContext context = createContext(caze, node);
				closeFlowNode(context, node, STATE_NODE.SEVERE);
			} catch (Throwable e) {
				log().e(nodeInfo,e);
				fireEvent.error(nodeInfo,e);
			}
		}
	}

	
	public boolean isExecuting(UUID nodeId) {
		synchronized (executing) {
			return executing.contains(nodeId);
		}
	}

	public void assignUserTask(UUID nodeId, String user) throws IOException, MException {
		PNode node = getFlowNode(nodeId);
		PCase caze = getCase(node.getCaseId());
		synchronized (caze) {
			if (node.getState() != STATE_NODE.WAITING) throw new MException("node is not WAITING",nodeId);
			if (node.getType() != TYPE_NODE.USER) throw new MException("node is not a user task",nodeId);
			if (node.getAssignedUser() != null) throw new MException("node is already assigned",nodeId,node.getAssignedUser());
			node.setAssignedUser(user);
			storage.saveFlowNode(node);
		}
	}

	public void unassignUserTask(UUID nodeId) throws IOException, MException {
		PNode node = getFlowNode(nodeId);
		PCase caze = getCase(node.getCaseId());
		synchronized (caze) {
			if (node.getState() != STATE_NODE.WAITING) throw new MException("node is not WAITING",nodeId);
			if (node.getType() != TYPE_NODE.USER) throw new MException("node is not a user task",nodeId);
			if (node.getAssignedUser() == null) throw new MException("node is not assigned",nodeId);
			node.setAssignedUser(null);
			storage.saveFlowNode(node);
		}
	}
	
	public void submitUserTask(UUID nodeId, IProperties values) throws IOException, MException {
		PNode node = getFlowNode(nodeId);
		PCase caze = getCase(node.getCaseId());
		synchronized (caze) {
			if (node.getState() != STATE_NODE.WAITING) throw new MException("node is not WAITING",nodeId);
			if (node.getType() != TYPE_NODE.USER) throw new MException("node is not a user task",nodeId);
			if (node.getAssignedUser() == null) throw new MException("node is not assigned",nodeId);
		
			EngineContext context = createContext(caze, node);
			AElement<?> aNode = context.getANode();
			if (!(aNode instanceof AUserTask<?>))
				throw new MException("node activity is not AUserTask",nodeId,aNode.getClass().getCanonicalName());
			
			((AUserTask<?>)aNode).doSubmit(values);
			
			node.setState(STATE_NODE.RUNNING);
			node.setScheduledNow();
			saveFlowNode(context, node, (AActivity<?>) aNode);
		}
	}

	public AElement<?> getANode(UUID nodeId) throws IOException, MException {
		PNode node = getFlowNode(nodeId);
		PCase caze = getCase(node.getCaseId());
		EngineContext context = createContext(caze, node);
		AElement<?> aNode = context.getANode();
		return aNode;
	}
	
	public PNodeInfo getFlowNodeInfo(UUID nodeId) throws Exception {
		synchronized (nodeCache) {
			PNode node = nodeCache.get(nodeId);
			if (node != null) {
				PCaseInfo caseInfo = getCaseInfo(node.getCaseId());
				return new PNodeInfo(caseInfo, node);
			}
		}
		return storage.loadFlowNodeInfo(nodeId);
	}

	public PNodeInfo storageGetFlowNodeInfo(UUID nodeId) throws Exception {
		return storage.loadFlowNodeInfo(nodeId);
	}

	public PCaseInfo getCaseInfo(UUID caseId) throws Exception {
		synchronized (caseCache) {
			PCase caze = caseCache.get(caseId);
			if (caze != null)
				return new PCaseInfo(caze);
		}
		return storage.loadCaseInfo(caseId);
	}
	
	public PCaseInfo storageGetCaseInfo(UUID caseId) throws Exception {
		return storage.loadCaseInfo(caseId);
	}
	
	public Object getCaseLock(PCaseInfo caseInfo) {
		return getCaseLock(caseInfo.getId());
	}
	
	public Object getCaseLock(PCase caze) {
		return getCaseLock(caze.getId());
	}
	
	public Object getCaseLock(PNode node) {
		return getCaseLock(node.getCaseId());
	}
	
	public Object getCaseLock(UUID caseId) {
		synchronized (cacheCaseLock) {
			Object lock = cacheCaseLock.get(caseId);
			if (lock == null) {
				lock = new Object();
				cacheCaseLock.put(caseId, lock);
			}
			return lock;
		}
	}
	
	public List<UUID> getLockedCases() {
		synchronized (cacheCaseLock) {
			LinkedList<UUID> out = new LinkedList<UUID>();

			for (Entry<UUID, Object> entry : cacheCaseLock.entrySet())
				if (MSystem.isLockedByThread(entry.getValue()))
							out.add(entry.getKey());
			return out;
		}
	}

	/* 
	 * Methods from InternalEngine
	 */
	
	@Override
	public RuntimeNode doExecuteStartPoint(ProcessContext<?> context, EElement eMyStartPoint) throws Exception {
		EngineContext eContext = (EngineContext)context;
		UUID flowId = createStartPoint(eContext, eMyStartPoint);
		PNode pNode = getFlowNode(flowId);
		PCase caze = getCase(pNode.getCaseId());
		EngineContext newContext = createContext(caze, pNode);
		RuntimeNode runtime = newContext.getARuntime();
		return runtime;
	}
	
}
