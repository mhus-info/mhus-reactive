package de.mhus.cherry.reactive.vaadin.core.space;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.vaadin.widgets.NodeList;
import de.mhus.lib.core.MApi;
import de.mhus.lib.vaadin.desktop.GuiLifecycle;
import de.mhus.lib.vaadin.desktop.Navigable;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class BpmSpace extends VerticalLayout implements GuiLifecycle, Navigable {

	private static final long serialVersionUID = 1L;
	private BpmSpaceService service;
	private IEngine engine;
	private NodeList nodeList;

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
		
		nodeList = new NodeList();
		SearchCriterias criterias = new SearchCriterias();
		String[] properties = new String[] {"*"};
		nodeList.configure(engine, criterias, properties);
	}

	@Override
	public void doDestroy() {
		
	}
	
}
