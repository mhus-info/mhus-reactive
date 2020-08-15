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

public class CaseLockProxy extends CaseLock implements PCaseLock {

    PCaseLock instance;
    private UUID caseId;
    private EngineListener fireEvent;
    private String stacktrace;

    public CaseLockProxy(
            PCaseLock instance, EngineListener fireEvent, String operation, Object... tagPairs) {
        super(false, operation, tagPairs);
        caseId = instance.getCaseId();
        fireEvent.lock(this, caseId);
        this.instance = instance;
        this.fireEvent = fireEvent;
        stacktrace = MCast.toString("Proxy " + caseId + " " + Thread.currentThread().getId());
        try {
            startSpan(getCase());
            scope.span().setTag("type", "proxy");
            scope.span().setTag("caseId", caseId.toString());
            scope.span().setTag("stacktrace", stacktrace);
        } catch (Throwable t) {
            log().d(caseId, t);
        }
    }

    @Override
    public PCase getCase() throws NotFoundException, IOException {
        return instance.getCase();
    }

    @Override
    public PNode getFlowNode(UUID id) throws NotFoundException, IOException {
        try {
            scope.span().log("getFlowNode " + id);
        } catch (Throwable t) {
        }
        return instance.getFlowNode(id);
    }

    @Override
    public void closeCase(boolean hard, int code, String msg)
            throws IOException, NotFoundException {
        try {
            scope.span().log("closeCase " + hard + " " + code + " " + msg);
        } catch (Throwable t) {
        }
        instance.closeCase(hard, code, msg);
    }

    @Override
    public void close() {
        if (instance == null) return;
        fireEvent.release(this, caseId);
        instance = null;
        fireEvent = null;
        super.close();
    }

    @Override
    public void saveFlowNode(PNode node) throws IOException, NotFoundException {
        try {
            scope.span().log("saveFlowNode " + node);
        } catch (Throwable t) {
        }
        instance.saveFlowNode(node);
    }

    @Override
    public void closeRuntime(UUID nodeId) throws MException, IOException {
        try {
            scope.span().log("closeRuntime " + nodeId);
        } catch (Throwable t) {
        }
        instance.closeRuntime(nodeId);
    }

    @Override
    public void closeFlowNode(EngineContext context, PNode pNode, STATE_NODE state)
            throws IOException, NotFoundException {
        try {
            scope.span().log("closeFlowNode " + pNode + " " + state);
        } catch (Throwable t) {
        }
        instance.closeFlowNode(context, pNode, state);
    }

    @Override
    public void saveRuntime(PNode pRuntime, RuntimeNode aRuntime) throws IOException {
        try {
            scope.span().log("saveRuntime " + pRuntime);
        } catch (Throwable t) {
        }
        instance.saveRuntime(pRuntime, aRuntime);
    }

    @Override
    public void savePCase(EngineContext context) throws IOException, NotFoundException {
        try {
            scope.span().log("savePCase");
        } catch (Throwable t) {
        }
        instance.savePCase(context);
    }

    @Override
    public void savePCase(APool<?> aPool, boolean init) throws IOException, NotFoundException {
        try {
            scope.span().log("savePCase " + init);
        } catch (Throwable t) {
        }
        instance.savePCase(aPool, init);
    }

    @Override
    public void doNodeErrorHandling(EngineContext context, PNode pNode, Throwable t) {
        try {
            scope.span().log("doNodeErrorHandling " + pNode + " " + t);
        } catch (Throwable tt) {
        }
        instance.doNodeErrorHandling(context, pNode, t);
    }

    @Override
    public PNode createActivity(EngineContext context, PNode previous, EElement start)
            throws Exception {
        try {
            scope.span().log("createActivity " + previous + " " + start);
        } catch (Throwable t) {
        }
        return instance.createActivity(context, previous, start);
    }

    @Override
    public void doNodeLifecycle(EngineContext context, PNode flow) throws Exception {
        try {
            scope.span().log("doNodeLifecycle " + flow);
        } catch (Throwable t) {
        }
        instance.doNodeLifecycle(context, flow);
    }

    @Override
    public UUID createStartPoint(EngineContext context, EElement start) throws Exception {
        try {
            scope.span().log("createStartPoint " + start);
        } catch (Throwable t) {
        }
        return instance.createStartPoint(context, start);
    }

    @Override
    public void saveFlowNode(EngineContext context, PNode flow, AActivity<?> activity)
            throws IOException, NotFoundException {
        try {
            scope.span().log("saveFlowNode " + flow);
        } catch (Throwable t) {
        }
        instance.saveFlowNode(context, flow, activity);
    }

    @Override
    public void doFlowNode(PNode pNode) {
        try {
            scope.span().log("doFlowNode " + pNode);
        } catch (Throwable t) {
        }
        instance.doFlowNode(pNode);
    }

    @Override
    public void setPCase(PCase pCase) throws MException {
        try {
            scope.span().log("setPCase " + pCase);
        } catch (Throwable t) {
        }
        instance.setPCase(pCase);
    }

    @Override
    public void resetPCase() {
        try {
            scope.span().log("resetPCase");
        } catch (Throwable t) {
        }
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
        try {
            scope.span().log("putRuntime " + id);
        } catch (Throwable t) {
        }
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

    @Override
    public long getOwnerThreadId() {
        return instance.getOwnerThreadId();
    }
}
