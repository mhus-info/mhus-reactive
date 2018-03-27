package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.Comparator;
import java.util.List;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsFactory;
import de.mhus.lib.vaadin.ExpandingTable;
import de.mhus.lib.vaadin.MhuTable;
import de.mhus.lib.vaadin.container.MhuBeanItemContainer;

public class NodeList extends MhuTable {

	private Log log = Log.getLog(NodeList.class);
	private static final long serialVersionUID = 1L;
	private String sortByDefault = "duedate";
	private boolean sortAscDefault = true;
	MhuBeanItemContainer<NodeItem> data = new MhuBeanItemContainer<NodeItem>(NodeItem.class);
	private SearchCriterias criterias;
	private String[] properties;
	
	private int lastExtend;
	private int page;
	private IEngine engine;
	private int size = 100;

	public NodeList() {
        setSizeFull();
        addStyleName("borderless");
        setSelectable(true);
        setTableEditable(false);
        setColumnCollapsingAllowed(true);
        setColumnReorderingAllowed(true);
        setSortContainerPropertyId(sortByDefault);
        setSortAscending(sortAscDefault);

    	MNls nls = new MNlsFactory().create(this);
    	data.configureTableByAnnotations(this, null, nls);

        addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				
			}
		});
        
        renderEventHandler().register(new RenderListener() {
			@Override
			public void onRender(ExpandingTable mhuTable, int first, int last) {
				doExtendTable(first, last);
			}
		});

	}
	
	private void doExtendTable(int first, int last) {
		int size = getItemIds().size() - 1;
		if (lastExtend < last && last == size) {
			lastExtend = last;
			doRefresh(++page);
		}
	}

	protected void doRefresh(int offset) {
				
		NodeContainer updatedData = getNodes(offset);
		if (updatedData != null) {
			if (data == null)
				data = updatedData;
			
			data.mergeAll(updatedData.getItemIds(), offset == 0 ? true : false, new Comparator<NodeItem>() {
				@Override
				public int compare(NodeItem o1, NodeItem o2) {
					return MSystem.equals(o1, o2) ? 0 : 1;
				}
			});
		}
		else {
			Notification.show("Daten konnten nicht abgefragt werden",Notification.Type.WARNING_MESSAGE);
			if (data == null)
				data = new NodeContainer();
			return;
		}
//		sortTable();
		if (offset == 0) {
			lastExtend = 0;
			page = 0;
			setCurrentPageFirstItemIndex(0);
			Notification.show("Liste aktualisiert",Notification.Type.TRAY_NOTIFICATION);
		}
	}

	private NodeContainer getNodes(int offset) {
		NodeContainer out = new NodeContainer();
		try {
			List<INode> list = engine.searchNodes(criterias, offset/size, size , properties);
			for (INode item : list)
				out.addItem(new NodeItem(item));
		} catch (Exception e) {
			log.w(e);
			Notification.show("Organisationen konnten nicht abgefragt werden", Type.WARNING_MESSAGE);
		}
		
		return out;
	}

	
	public void configure(IEngine engine, SearchCriterias criterias, String[] properties) {
		this.engine = engine;
		this.criterias = criterias;
		this.properties = properties;
		
	}
	
	
}
