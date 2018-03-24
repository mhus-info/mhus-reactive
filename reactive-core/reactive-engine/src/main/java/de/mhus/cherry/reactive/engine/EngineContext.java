package de.mhus.cherry.reactive.engine;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.engine.AaaProvider;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EEngine;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.errors.MException;

public class EngineContext extends MLog implements ProcessContext<APool<?>>{

	private Engine engine;
	
	private PCase    pCase; // Persistent case object
	private EPool    ePool; // pool descriptor, defined in PCase, provided by ProcessProvider
	private APool<?> aPool; // Pool Model object
	
	private PNode 	    pNode; // Persistent flow node object
	private EElement    eNode; // Flow Node descriptor, defined in PNode, provided by EPool
	private AElement<?> aNode; // Flow Node Model object

	private String uri;

	private EProcess eProcess;

	private PNode pRuntime;

	private RuntimeNode aRuntime;

	private ASwimlane<APool<?>> aLane;

	public EngineContext(Engine engine) {
		this.engine = engine;
	}
		
	public EngineContext(Engine engine, PNode pNode) {
		this.engine = engine;
		this.pNode = pNode;
	}
	
	public EngineContext(EngineContext parent, PNode pNode) {
		this.engine = parent.engine;
		this.eProcess = parent.eProcess;
		this.aRuntime = parent.aRuntime;
		this.pRuntime = parent.pRuntime;
		this.pCase = parent.pCase;
		this.uri = parent.uri;
		this.ePool = parent.ePool;
		this.aPool = parent.aPool;
		
		this.pNode = pNode;

	}

//	synchronized void setPNode(PNode pNode) {
//		this.pNode = pNode;
//		eNode = null;
//		pRuntime = null;
//		aRuntime = null;
//		aLane = null;
//	}

	@Override
	public synchronized APool<?> getPool() {
		if (aPool == null)
			try {
				aPool = engine.createPoolObject(getEPool());
				if (aPool instanceof ContextRecipient)
					((ContextRecipient)aPool).setContext(this);
				aPool.importParameters(getPCase().getParameters());
			} catch (InstantiationException | IllegalAccessException e) {
				log().e(e);
			}
		return aPool;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ASwimlane<APool<?>> getSwimlane() {
		if (aLane == null) {
			try {
				aLane = (ASwimlane<APool<?>>) engine.createSwimlaneObject(this, getENode());
				if (aLane instanceof ContextRecipient)
					((ContextRecipient)aLane).setContext(this);
			} catch (InstantiationException | IllegalAccessException e) {
				log().w(e);
			}
		}
		return aLane;
	}

	@Override
	public PCase getPCase() {
		return pCase;
	}

	void setPCase(PCase pCase) {
		this.pCase = pCase;
	}

	@Override
	public EPool getEPool() {
		return ePool;
	}

	public void setEPool(EPool ePool) {
		this.ePool = ePool;
	}

	void setAPool(APool<?> aPool) {
		this.aPool = aPool;
	}

	@Override
	public PNode getPNode() {
		return pNode;
	}

	@Override
	public synchronized EElement getENode() {
		if (eNode == null) {
			eNode = ePool.getElement(pNode.getCanonicalName());
		}
		return eNode;
	}

	void setENode(EElement eNode) {
		this.eNode = eNode;
	}

	@Override
	public synchronized AElement<?> getANode() {
		if (aNode == null) {
			try {
				aNode = engine.createActivityObject(getENode());
				if (aNode instanceof ContextRecipient)
					((ContextRecipient)aNode).setContext(this);
			} catch (InstantiationException | IllegalAccessException e) {
				log().w(e);
			}
		}
		return aNode;
	}

	void setANode(AElement<?> aNode) {
		this.aNode = aNode;
	}

	Engine getEngine() {
		return engine;
	}

	@Override
	public EEngine getEEngine() {
		return engine;
	}
	
	void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
	public String getUri() {
		return uri;
	}

	public void setEProcess(EProcess eProcess) {
		this.eProcess = eProcess;
	}
	
	@Override
	public EProcess getEProcess() {
		return eProcess;
	}

	void setPRuntime(PNode runtime) {
		this.pRuntime = runtime;
	}
	
	@Override
	public PNode getPRuntime() {
		return pRuntime;
	}

	@Override
	public synchronized RuntimeNode getARuntime() {
		if (aRuntime == null) {
			if (pRuntime == null) {
				pRuntime = engine.getRuntimeForPNode(this, pNode);
				if (pRuntime == null)
					throw new NullPointerException("PRuntime not found for " + this);
			}
			aRuntime = engine.createRuntimeObject(this, pRuntime);
			if (aRuntime instanceof ContextRecipient)
				((ContextRecipient)aRuntime).setContext(this);
		}
		return aRuntime;
	}
	
	@Override
	public String toString() {
		return MSystem.toString(this, uri, pNode);
	}

	@Override
	public PNode createActivity(Class<? extends AActivity<?>> next) throws Exception {
		// check if defined
		boolean outFound = false;
		for (Output output : getENode().getOutputs())
			if (next == output.activity()) {
				outFound = true;
				break;
			}
		if (!outFound) {
			for (Trigger trigger : getENode().getTriggers())
				if (trigger.activity() == next) {
					outFound = true;
					break;
				}
		}
		if (!outFound)
			log().w("create undefined following activity",getENode(),next);
		
		EElement start = getEPool().getElement(next.getCanonicalName());
		return engine.createActivity(this, getPNode(), start);
	}

	@Override
	public void saveRuntime() throws IOException {
		engine.saveRuntime(getPRuntime(), aRuntime);
	}

	@Override
	public AaaProvider getAaaProvider() {
		return engine.getAaaProvider();
	}

	@Override
	public void doCloseActivity(RuntimeNode runtimeNode, UUID nodeId) throws MException, IOException {
		engine.doCloseActivity(runtimeNode, nodeId);
	}

	@Override
	public Object getLock() {
		if (pNode != null)
			return engine.getCaseLock(pNode.getCaseId());
		if (pCase != null)
			return engine.getCaseLock(pCase.getId());
		return engine;
	}

}
