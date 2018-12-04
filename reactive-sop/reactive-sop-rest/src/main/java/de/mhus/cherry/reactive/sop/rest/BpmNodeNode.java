/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import de.mhus.osgi.sop.api.rest.ObjectListNode;
import de.mhus.osgi.sop.api.rest.CallContext;
import de.mhus.osgi.sop.api.rest.RestNodeService;

@Component(provide=RestNodeService.class)
public class BpmNodeNode extends ObjectListNode<INode> {

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

		String propertyNames = callContext.getParameter("_names");

		SearchCriterias criterias = new SearchCriterias(new MProperties(callContext.getParameters()));
		int page = M.c(callContext.getParameter("_page"), 0);
		int size = Math.min(M.c(callContext.getParameter("_size"), 100), 1000);
		try {
			return engine.searchNodes(criterias, page, size, propertyNames == null ? null : propertyNames.split(","));
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

		String propertyNames = context.getParameter("_names");

		return engine.getNode(id, propertyNames == null ? null : propertyNames.split(","));
	}

}
