package de.mhus.cherry.reactive.model.engine;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import de.mhus.lib.errors.NotFoundException;

public interface CaseLock extends Closeable {

    PCase getCase() throws NotFoundException, IOException;
    
    PNode getFlowNode(UUID id) throws NotFoundException, IOException;
    
    default PNode getFlowNode(PNodeInfo nodeInfo) throws NotFoundException, IOException {
        return getFlowNode(nodeInfo.getId());
    }
    
    void closeCase(boolean hard, int code, String msg) throws IOException, NotFoundException;

    @Override
    public void close();

    void saveFlowNode(PNode node) throws IOException, NotFoundException;
    
}
