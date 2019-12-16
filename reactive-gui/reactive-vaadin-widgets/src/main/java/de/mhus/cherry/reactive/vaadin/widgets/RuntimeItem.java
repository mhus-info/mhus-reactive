package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.Date;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.EngineMessage;
import de.mhus.cherry.reactive.model.engine.EngineMessage.TYPE;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.lib.annotations.vaadin.Column;

public class RuntimeItem {

    @SuppressWarnings("unused")
    private IEngine engine;
    private EngineMessage item;

    public RuntimeItem() {
    }
    
    public RuntimeItem(IEngine engine, EngineMessage item) {
        this.engine = engine;
        this.item = item;
    }

    @Column(order=1,title="Date")
    public Date getDate() {
        if (item == null) return null;
        return new Date(item.getTimestamp());
    }
    
    @Column(order=2,title="Type")
    public TYPE getType() {
        if (item == null) return null;
        return item.getType();
    }
    
    @Column(order=3,title="From")
    public UUID getFromNode() {
        if (item == null) return null;
        return item.getFromNode();
    }
    
    @Column(order=4,title="To")
    public UUID getToNode() {
        if (item == null) return null;
        return item.getToNode();
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

}
