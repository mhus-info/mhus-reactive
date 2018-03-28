package de.mhus.cherry.reactive.vaadin.core.space;

import java.io.IOException;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.vaadin.widgets.NodeItem;
import de.mhus.cherry.reactive.vaadin.widgets.VHumanForm;
import de.mhus.cherry.reactive.vaadin.widgets.VNodeList;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.MException;
import de.mhus.lib.vaadin.desktop.GuiLifecycle;
import de.mhus.lib.vaadin.desktop.Navigable;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class BpmSpace extends VerticalLayout implements GuiLifecycle, Navigable {

	private static final long serialVersionUID = 1L;
	private BpmSpaceService service;
	private IEngine engine;
	private VNodeList nodeList;

	public BpmSpace(BpmSpaceService bpmSpaceService) {
		this.service = bpmSpaceService;
	}

	@Override
	public String navigateTo(String selection, String filter) {
		return null;
	}

	@Override
	public void onShowSpace(boolean firstTime) {
		
	}

	@Override
	public void doInitialize() {
		AccessApi aaa = MApi.lookup(AccessApi.class);
		AaaContext context = aaa.getCurrent();
		engine = MApi.lookup(IEngineFactory.class).create(context.getAccountId(), context.getLocale());
		
		nodeList = new VNodeList() {
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
		SearchCriterias criterias = new SearchCriterias();
		String[] properties = new String[] {"*"};
		nodeList.configure(engine, criterias, properties);
		
        addComponent(nodeList);
        setExpandRatio(nodeList, 1);

	}

	protected void showHumanForm(NodeItem selected) throws Exception {

		INode node = engine.getNode(selected.getId().toString(), null);
		VHumanForm form = new VHumanForm(node) {
			@Override
			protected void onFormCancel() {
				showNodeList();
			}
			@Override
			protected void onFormSubmit(INode node, MProperties properties) {
				try {
					node.submitHumanTask(properties);
					showNodeList();
				} catch (IOException | MException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		removeAllComponents();
		addComponent(form);
		setExpandRatio(form, 1);
	}

	protected void showNodeList() {
		removeAllComponents();
		addComponent(nodeList);
        setExpandRatio(nodeList, 1);
	}

	@Override
	public void doDestroy() {
		
	}
	
}
