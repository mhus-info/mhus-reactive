package de.mhus.cherry.reactive.engine.util;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EngineListener;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public class CaseLockProxy implements PCaseLock {

    PCaseLock instance;
    private UUID caseId;
    private EngineListener fireEvent;
    private String stacktrace;
    
    public CaseLockProxy(PCaseLock instance, EngineListener fireEvent) {
        caseId = instance.getCaseId();
        fireEvent.lock(this,caseId);
        this.instance = instance;
        this.fireEvent = fireEvent;
        stacktrace = MCast.toString("Proxy " + caseId + " " + Thread.currentThread().getId());
    }

    @Override
    public PCase getCase() throws NotFoundException, IOException {
        return instance.getCase();
    }

    @Override
    public PNode getFlowNode(UUID id) throws NotFoundException, IOException {
        return instance.getFlowNode(id);
    }

    @Override
    public void closeCase(boolean hard, int code, String msg) throws IOException, NotFoundException {
        instance.closeCase(hard, code, msg);
    }

    @Override
    public void close() {
        if (instance == null) return;
        fireEvent.release(this,caseId);
        instance = null;
        fireEvent = null;
    }

    @Override
    public void saveFlowNode(PNode node) throws IOException, NotFoundException {
        instance.saveFlowNode(node);
    }

    @Override
    public void closeRuntime(UUID nodeId) throws MException, IOException {
        instance.closeRuntime(nodeId);
    }

    @Override
    public void closeFlowNode(EngineContext context, PNode pNode, STATE_NODE state)
            throws IOException, NotFoundException {
        instance.closeFlowNode(context, pNode, state);
    }

    @Override
    public void saveRuntime(PNode pRuntime, RuntimeNode aRuntime) throws IOException {
        instance.saveRuntime(pRuntime, aRuntime);
    }

    @Override
    public void savePCase(EngineContext context) throws IOException, NotFoundException {
        instance.savePCase(context);
    }

    @Override
    public void savePCase(APool<?> aPool, boolean init) throws IOException, NotFoundException {
        instance.savePCase(aPool, init);
    }

    @Override
    public void doNodeErrorHandling(EngineContext context, PNode pNode, Throwable t) {
        instance.doNodeErrorHandling(context, pNode, t);
    }

    @Override
    public PNode createActivity(EngineContext context, PNode previous, EElement start) throws Exception {
        return instance.createActivity(context, previous, start);
    }

    @Override
    public void doNodeLifecycle(EngineContext context, PNode flow) throws Exception {
        instance.doNodeLifecycle(context, flow);
    }

    @Override
    public UUID createStartPoint(EngineContext context, EElement start) throws Exception {
        return instance.createStartPoint(context, start);
    }

    @Override
    public void saveFlowNode(EngineContext context, PNode flow, AActivity<?> activity)
            throws IOException, NotFoundException {
        instance.saveFlowNode(context, flow, activity);
    }

    @Override
    public void doFlowNode(PNode pNode) {
        instance.doFlowNode(pNode);
    }

    @Override
    public void setPCase(PCase pCase) throws MException {
        instance.setPCase(pCase);
    }

    @Override
    public void resetPCase() {
        instance.resetPCase();
    }

    @Override
    public UUID getCaseId() {
        return caseId;
    }
    
    @Override
    public String toString() {
        return MSystem.toString(this, caseId);
    }

    @Override
    public RuntimeNode getRuntime(UUID nodeId) {
        return instance.getRuntime(nodeId);
    }

    @Override
    public void putRuntime(UUID id, RuntimeNode runtime) {
        instance.putRuntime(id, runtime);
    }

    @Override
    public Lock getLock() {
        return instance.getLock();
    }

    @Override
    public String getStartStacktrace() {
        return stacktrace;
    }
}
