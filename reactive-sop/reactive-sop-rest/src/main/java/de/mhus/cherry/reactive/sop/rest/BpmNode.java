package de.mhus.cherry.reactive.sop.rest;

import java.util.List;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.lib.core.MApi;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotSupportedException;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;
import de.mhus.osgi.sop.api.rest.AbstractObjectListNode;
import de.mhus.osgi.sop.api.rest.CallContext;
import de.mhus.osgi.sop.api.rest.JsonResult;
import de.mhus.osgi.sop.api.rest.RestNodeService;

@Component(provide=RestNodeService.class)
public class BpmNode extends AbstractObjectListNode<Object> {

	@Override
	public String[] getParentNodeIds() {
		return new String[] {ROOT_ID,PUBLIC_ID,FOUNDATION_ID};
	}

	@Override
	public String getNodeId() {
		return "bpm";
	}

	@Override
	protected List<Object> getObjectList(CallContext callContext) throws MException {
		return null;
	}

	@Override
	public Class<Object> getManagedClass() {
		return Object.class;
	}

	@Override
	protected Object getObjectForId(CallContext context, String id) throws Exception {
		return null;
	}

	@Override
	protected void doUpdate(JsonResult result, CallContext callContext) throws Exception {
		throw new NotSupportedException();
	}
	
	@Override
	protected void doCreate(JsonResult result, CallContext callContext) throws Exception {
		AccessApi aaa = MApi.lookup(AccessApi.class);
		AaaContext context = aaa.getCurrent();
		IEngine engine = MApi.lookup(IEngineFactory.class).create(context.getAccountId(), context.getLocale());
		
		String uri = callContext.getParameter("uri");
		String res = String.valueOf(engine.execute(uri));
		
		result.createObjectNode().put("result", res);
	}
	
	@Override
	protected void doDelete(JsonResult result, CallContext callContext) throws Exception {
		throw new NotSupportedException();
	}

}
