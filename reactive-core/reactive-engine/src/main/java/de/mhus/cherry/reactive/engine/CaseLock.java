package de.mhus.cherry.reactive.engine;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.lib.errors.NotFoundException;

public interface CaseLock extends Closeable {

    PCase getCase() throws NotFoundException, IOException;
    
    PNode getFlowNode(UUID id) throws NotFoundException, IOException;
    
    default PNode getFlowNode(PNodeInfo nodeInfo) {
        return getFlowNode(nodeInfo.getId());
    }
    
    void closeCase(boolean hard, int code, String msg) throws IOException;

    @Override
    public void close();
    
}
