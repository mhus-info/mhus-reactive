package de.mhus.cherry.reactive.sop.rest;

import java.io.IOException;
import java.util.List;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;
import de.mhus.osgi.sop.api.rest.AbstractObjectListNode;
import de.mhus.osgi.sop.api.rest.CallContext;
import de.mhus.osgi.sop.api.rest.RestNodeService;

@Component(provide=RestNodeService.class)
public class BpmNodeNode extends AbstractObjectListNode<INode> {

	@Override
	public String[] getParentNodeIds() {
		return new String[] {ROOT_ID,FOUNDATION_ID};
	}

	@Override
	public String getNodeId() {
		return "bpmnode";
	}

	@Override
	protected List<INode> getObjectList(CallContext callContext) throws MException {

		AccessApi aaa = MApi.lookup(AccessApi.class);
		AaaContext context = aaa.getCurrent();
		IEngine engine = MApi.lookup(IEngineFactory.class).create(context.getAccountId(), context.getLocale());

		SearchCriterias criterias = new SearchCriterias(new MProperties(callContext.getParameters()));
		int page = M.c(callContext.getParameter("_page"), 0);
		int size = Math.min(M.c(callContext.getParameter("_size"), 100), 1000);
		try {
			return engine.searchNodes(criterias, page, size);
		} catch (IOException e) {
			throw new MException(e);
		}
	}

	@Override
	public Class<INode> getManagedClass() {
		return INode.class;
	}

	@Override
	protected INode getObjectForId(CallContext context, String id) throws Exception {
		
		AccessApi aaa = MApi.lookup(AccessApi.class);
		AaaContext acontext = aaa.getCurrent();
		IEngine engine = MApi.lookup(IEngineFactory.class).create(acontext.getAccountId(), acontext.getLocale());

		String properties = context.getParameter("_properties");

		return engine.getNode(id, properties == null ? null : properties.split(","));
	}

}
