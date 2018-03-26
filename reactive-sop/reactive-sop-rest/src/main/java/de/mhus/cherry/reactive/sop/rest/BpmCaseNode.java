package de.mhus.cherry.reactive.sop.rest;

import java.util.List;

import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.rest.AbstractObjectListNode;
import de.mhus.osgi.sop.api.rest.CallContext;

public class BpmCaseNode extends AbstractObjectListNode<BpmCase> {

	@Override
	public String[] getParentNodeIds() {
		return new String[] {FOUNDATION_ID};
	}

	@Override
	public String getNodeId() {
		return "bpmcase";
	}

	@Override
	protected List<BpmCase> getObjectList(CallContext callContext) throws MException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<BpmCase> getManagedClass() {
		return BpmCase.class;
	}

	@Override
	protected BpmCase getObjectForId(CallContext context, String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
