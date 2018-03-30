package de.mhus.cherry.reactive.vaadin.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.vaadin.widgets.NodeItem;
import de.mhus.cherry.reactive.vaadin.widgets.VCaseList;
import de.mhus.cherry.reactive.vaadin.widgets.VHumanForm;
import de.mhus.cherry.reactive.vaadin.widgets.VNodeList;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.errors.MException;
import de.mhus.lib.vaadin.desktop.GuiLifecycle;
import de.mhus.lib.vaadin.desktop.GuiSubSpace;
import de.mhus.lib.vaadin.desktop.Navigable;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class BpmSpace extends VerticalLayout implements GuiLifecycle, Navigable {

	private static Log log = Log.getLog(BpmSpace.class);
	private static final long serialVersionUID = 1L;
	private static final String I_DEFAULT = "Default";
	private static final String I_UNASSIGNED = "Unassigned";
	private static final String I_ASSIGNED = "Assigned";
	private static final String DEFAULT_MENU_SELECTION = I_UNASSIGNED;
	private static final String I_ALL_NODES = "All nodes";
	private static final String I_ACTIVE_CASES = "Active cases";
	private static final String I_ALL_CASES = "All cases";
	private BpmSpaceService service;
	private IEngine engine;
	private HorizontalLayout page;

	private String currentSelection;
	private Component currentView;
	private Map<String, Component> contentCache;
	private String currentFilter;

	public BpmSpace(BpmSpaceService bpmSpaceService) {
		this.service = bpmSpaceService;
	}

	@Override
	public String navigateTo(String selection, String filter) {
		Tree tree = null;
		try {
			tree = (Tree)(((VerticalLayout)(page.getComponent(0))).getComponent(0));
		} catch (Exception e) {
			log.w("failed changing menu selection", e);
			return null;
		}
		
		if (selection != null && tree.containsId(selection)) {
			
			switch (selection) {
			case I_UNASSIGNED: {
				SearchCriterias criterias = new SearchCriterias();
				criterias.unassigned = true;
				criterias.nodeState = STATE_NODE.WAITING;
				criterias.type = TYPE_NODE.HUMAN;
				String[] properties = new String[] {"*"};
				Component cached = contentCache.get(selection);
				if (cached == null) {
					cached = getNodeListView(criterias, properties);
					if (cached == null) return null;
					contentCache.put(selection, cached);
				} else
				if (cached instanceof VNodeList) {
					((VNodeList)cached).doReload();
				}
				currentView = cached;
				setContent(cached);
			 } break;
			case I_ASSIGNED: {
				initEngine();
				if (engine == null) return null;
				SearchCriterias criterias = new SearchCriterias();
				criterias.assigned = engine.getUser();
				criterias.nodeState = STATE_NODE.WAITING;
				criterias.type = TYPE_NODE.HUMAN;
				String[] properties = new String[] {"*"};
				Component cached = contentCache.get(selection);
				if (cached == null) {
					cached = getNodeListView(criterias, properties);
					if (cached == null) return null;
					contentCache.put(selection, cached);
				} else
				if (cached instanceof VNodeList) {
					((VNodeList)cached).doReload();
				}
				currentView = cached;
				setContent(cached);
			 } break;			
			 case I_ALL_NODES: {
				SearchCriterias criterias = new SearchCriterias();
				String[] properties = new String[] {"*"};
				setContent(getNodeListView(criterias, properties));
			 } break;
			 case I_ALL_CASES: {
				SearchCriterias criterias = new SearchCriterias();
				String[] properties = new String[] {"*"};
				setContent(getCaseListView(criterias, properties));
			 } break;
			 case I_ACTIVE_CASES: {
				SearchCriterias criterias = new SearchCriterias();
				criterias.caseState = STATE_CASE.RUNNING;
				String[] properties = new String[] {"*"};
				setContent(getCaseListView(criterias, properties));
			 } break;
			}
			
			currentSelection = selection;
			currentFilter = filter;
			tree.select(selection);
			
				
			if (tree.getParent(selection) != null && !tree.isExpanded(tree.getParent(selection)))
				tree.expandItem(tree.getParent(selection));
			
			if (MString.isSet(filter)) {
//TODO				currentView.setFilter(filter);
			}
			return null;
			
		} else {
			log.w("failed changing menu selection, no such element found", selection);
			return null;
		}
		
	}

	@Override
	public void onShowSpace(boolean firstTime) {
		
	}

	private void setContent(Component content) {
		if (content == null) return;
		try {
			Component oldContent = page.getComponent(1);
			if (oldContent != content) {
				page.replaceComponent(oldContent, content);
				page.setExpandRatio(content, 1);
			}
		} catch (IndexOutOfBoundsException e) {
			page.addComponent(content, 1);
			page.setExpandRatio(content, 1);
		}
	}

	@Override
	public void doInitialize() {
		
		contentCache = new HashMap<>();

		page = new HorizontalLayout();
		page.setSizeFull();
		
		VerticalLayout menu = buildMenu();
		
		page.addComponent(menu);
		page.setExpandRatio(menu, 0);
//		page.setMargin(true);
		
		navigateTo(DEFAULT_MENU_SELECTION, null);
		
		addComponent(page);

	}
	
	private VerticalLayout buildMenu() {
			
		VerticalLayout menu = new VerticalLayout();
		
		menu.setSizeFull();
		menu.setWidth(200f, Unit.PIXELS);
		menu.setMargin(new MarginInfo(true, false, true, false));
		menu.setStyleName("leftmenu");
		final Tree tree = new Tree();

		// structure
		tree.addItem(I_DEFAULT);
		
		tree.addItem(I_UNASSIGNED);
		tree.setParent(I_UNASSIGNED, I_DEFAULT);
		tree.setChildrenAllowed(I_UNASSIGNED, false);

		tree.addItem(I_ASSIGNED);
		tree.setParent(I_ASSIGNED, I_DEFAULT);
		tree.setChildrenAllowed(I_ASSIGNED, false);

		tree.addItem(I_ALL_NODES);
		tree.setParent(I_ALL_NODES, I_DEFAULT);
		tree.setChildrenAllowed(I_ALL_NODES, false);

		tree.addItem(I_ACTIVE_CASES);
		tree.setParent(I_ACTIVE_CASES, I_DEFAULT);
		tree.setChildrenAllowed(I_ACTIVE_CASES, false);

		tree.addItem(I_ALL_CASES);
		tree.setParent(I_ALL_CASES, I_DEFAULT);
		tree.setChildrenAllowed(I_ALL_CASES, false);
		
		
		// end
		
		tree.setNullSelectionAllowed(false);
        
        tree.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object selected = event.getProperty().getValue();
				if (selected != null && !selected.equals(currentSelection)) {
					String value = String.valueOf(selected);
					// ensure ACS does not stay in selected state
					navigateTo(value, null);
				}
			}
		});
        
        tree.setSizeFull();
        tree.expandItemsRecursively(I_DEFAULT);
        menu.addComponent(tree);
        menu.setExpandRatio(tree, 1);
        
		return menu;
	}

	private Component getNodeListView(SearchCriterias criterias, String[] properties) {
		initEngine();
		if (engine == null) return null;
		VNodeList list = new VNodeList() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void doOpenHumanForm(NodeItem selected) {
				try {
					showHumanForm(selected);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		list.configure(engine, criterias, properties);
        addComponent(list);
        setExpandRatio(list, 1);
        
        return list;
	}

	private Component getCaseListView(SearchCriterias criterias, String[] properties) {
		initEngine();
		if (engine == null) return null;
		VCaseList list = new VCaseList();
		list.configure(engine, criterias, properties);
        addComponent(list);
        setExpandRatio(list, 1);
        
        return list;
	}
	
	private void initEngine() {
		AccessApi aaa = MApi.lookup(AccessApi.class);
		AaaContext context = aaa.getCurrent();
		if (context == null) return;
		engine = MApi.lookup(IEngineFactory.class)
				.create(
						context.getAccountId(), 
						context.getLocale());
	}

	protected void showHumanForm(NodeItem selected) throws Exception {

		INode node = engine.getNode(selected.getId().toString(), null);
		VHumanForm form = new VHumanForm(node) {
			@Override
			protected void onFormCancel() {
				System.out.println("Cancel");
				showNodeList();
			}
			@Override
			protected void onFormSubmit(INode node, MProperties properties) {
				System.out.println("Submit");
				try {
					node.submitHumanTask(properties);
					showNodeList();
				} catch (IOException | MException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		setContent(form);
	}

	protected void showNodeList() {
		navigateTo(currentSelection, currentFilter);
	}

	@Override
	public void doDestroy() {
		contentCache = null;
		page.detach();
		page = null;
	}
	
}
