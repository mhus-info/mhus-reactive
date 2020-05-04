package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.EngineMessage;
import de.mhus.cherry.reactive.model.engine.EngineMessage.TYPE;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.annotations.vaadin.Column;
import de.mhus.lib.core.util.DateTime;

public class RuntimeItem {

    private EngineMessage item;
    private VRuntimeDetails details;

    public RuntimeItem() {
    }
    
    public RuntimeItem(VRuntimeDetails details, EngineMessage item) {
        this.details = details;
        this.item = item;
    }

    @Column(order=1,title="Date")
    public DateTime getDate() {
        if (item == null) return null;
        return new DateTime(item.getTimestamp());
    }
    
    @Column(order=2,title="Type")
    public TYPE getType() {
        if (item == null) return null;
        return item.getType();
    }
    
    @Column(order=3,title="From")
    public String getFromNode() {
        if (item == null) return null;
        INode node = details.getNode(item.getFromNode());
        return node == null ? "" : node.getCanonicalName();
    }
    
    @Column(order=4,title="To")
    public String getToNode() {
        if (item == null) return null;
        INode node = details.getNode(item.getToNode());
        return node == null ? "" : node.getCanonicalName();
    }
    
    @Column(order=5,title="Message")
    public String getMessage() {
        if (item == null) return null;
        return item.getMessage();
    }

    @Column(order=6,title="Ident", editable=false)
    public String getServerIdent() {
        if (item == null) return null;
        return item.getServerIdent();
    }

    @Column(order=7,title="From Id",elapsed=false)
    public UUID getFromNodeId() {
        if (item == null) return null;
        return item.getFromNode();
    }
    
    @Column(order=7,title="To Id",elapsed=false)
    public UUID getToNodeId() {
        if (item == null) return null;
        return item.getToNode();
    }

}
