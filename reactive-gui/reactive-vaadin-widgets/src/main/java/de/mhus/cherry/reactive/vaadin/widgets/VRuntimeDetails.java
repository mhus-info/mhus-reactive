package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.vaadin.event.Action;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;

import de.mhus.cherry.reactive.model.engine.EngineMessage;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsFactory;
import de.mhus.lib.core.util.SoftHashMap;
import de.mhus.lib.vaadin.MhuTable;
import de.mhus.lib.vaadin.container.MhuBeanItemContainer;

@SuppressWarnings("deprecation")
public class VRuntimeDetails extends MhuTable {

    private Log log = Log.getLog(VRuntimeDetails.class);
    private static final long serialVersionUID = 1L;
    MhuBeanItemContainer<RuntimeItem> data = new MhuBeanItemContainer<>(RuntimeItem.class);
    private SoftHashMap<UUID, INode> nodeCache = new SoftHashMap<>();
    
    private IEngine engine;
    private List<EngineMessage[]> messages;

    public VRuntimeDetails() {
    }
    
    public void configure(IEngine engine, List<EngineMessage[]> messages) {
        this.engine = engine;
        this.messages = messages;
        
        data = getItems();
        setSizeFull();
        addStyleName("borderless");
        setSelectable(true);
        setTableEditable(false);
        setColumnCollapsingAllowed(true);
        setColumnReorderingAllowed(true);
        if (data != null) {
            data.removeAllContainerFilters();
            setContainerDataSource(data);
            MNls nls = new MNlsFactory().create(this);
            data.configureTableByAnnotations(this, null, nls);
        }
        
        addItemClickListener(new ItemClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                }
            }
        });
        
        addActionHandler(new Action.Handler() {
            private static final long serialVersionUID = 1L;
            @Override
            public Action[] getActions(Object target, final Object sender) {
                LinkedList<Action> list = new LinkedList<>();
                Collection<?> targets = getSelectedValues();
                if (targets != null && targets.size() > 0)
                    target = targets.iterator().next();
                
                if (target != null) {
                }
                return list.toArray(new Action[list.size()]);
            }
            @Override
            public void handleAction(final Action action, final Object sender,
                    final Object target) {
                try {
                } catch (Throwable t) {
                    log.e(t);
                }
            }
        });
        
        setDragMode(TableDragMode.NONE);
        setMultiSelect(false);

//        setImmediate(true);
        
    }

    private RuntimeContainer getItems() {
        
        RuntimeContainer out = new RuntimeContainer();
        boolean first = true;
        for (EngineMessage[] runtime : messages) {
            if (!first)
                out.addItem(new RuntimeItem());
            first = false;
            for (EngineMessage msg : runtime)
                out.addItem(new RuntimeItem(this, msg));
        }
        return out;
    }

    public INode getNode(UUID nodeId) {
        if (nodeId == null) return null;
        try {
            INode node = nodeCache.get(nodeId);
            if (node == null) {
                node = engine.getNode(nodeId.toString());
                nodeCache.put(nodeId, node);
            }
            return node;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
