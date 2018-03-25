package de.mhus.cherry.reactive.vaadin.widgets;

import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsFactory;
import de.mhus.lib.vaadin.ExpandingTable;
import de.mhus.lib.vaadin.MhuTable;
import de.mhus.lib.vaadin.container.MhuBeanItemContainer;

public class NodeList extends MhuTable {

	private static final long serialVersionUID = 1L;
	private String sortByDefault = "duedate";
	private boolean sortAscDefault = true;
	MhuBeanItemContainer<NodeItem> data = new MhuBeanItemContainer<NodeItem>(NodeItem.class);
	
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
//				doExtendTable(mhuTable, first, last);
			}
		});

	}
}
