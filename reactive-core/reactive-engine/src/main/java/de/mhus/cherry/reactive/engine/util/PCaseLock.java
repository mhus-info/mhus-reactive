package de.mhus.cherry.reactive.engine.util;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.engine.CaseLock;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public interface PCaseLock extends CaseLock {

    void closeRuntime(UUID nodeId) throws MException, IOException;

    void closeFlowNode(EngineContext context, PNode pNode, STATE_NODE state) throws IOException, NotFoundException;

    void saveRuntime(PNode pRuntime, RuntimeNode aRuntime) throws IOException;

    void savePCase(EngineContext context) throws IOException, NotFoundException;

    void savePCase(APool<?> aPool, boolean init) throws IOException, NotFoundException;

    void doNodeErrorHandling(EngineContext context, PNode pNode, Throwable t);

    PNode createActivity(EngineContext context, PNode previous, EElement start) throws Exception;

    void doNodeLifecycle(EngineContext context, PNode flow) throws Exception;

    UUID createStartPoint(EngineContext context, EElement start) throws Exception;

    void saveFlowNode(EngineContext context, PNode flow, AActivity<?> activity) throws IOException, NotFoundException;

    void doFlowNode(PNode pNode);

    void setPCase(PCase pCase) throws MException;

    void resetPCase();

    UUID getCaseId();
    
    long getOwnerThreadId();

}
