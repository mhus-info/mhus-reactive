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
/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
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
