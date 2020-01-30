/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.sop.rest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.lib.core.M;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotSupportedException;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;
import de.mhus.osgi.sop.api.rest.ObjectListNode;
import de.mhus.osgi.sop.api.rest.CallContext;
import de.mhus.osgi.sop.api.rest.JsonResult;
import de.mhus.osgi.sop.api.rest.RestNodeService;

@Component(service = RestNodeService.class)
public class BpmNode extends ObjectListNode<Object, Object> {

    @Override
    public String[] getParentNodeCanonicalClassNames() {
        return new String[] {ROOT_PARENT, PUBLIC_PARENT, FOUNDATION_PARENT};
    }

    @Override
    public String getNodeId() {
        return "bpm";
    }

    @Override
    protected List<Object> getObjectList(CallContext callContext) throws MException {
        return null;
    }

    //	@Override
    //	public Class<Object> getManagedClass() {
    //		return Object.class;
    //	}

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
        AccessApi aaa = M.l(AccessApi.class);
        AaaContext context = aaa.getCurrent();
        IEngine engine =
                M.l(IEngineFactory.class).create(context.getAccountId(), context.getLocale());

        String uri = callContext.getParameter("uri");
        String res = String.valueOf(engine.doExecute(uri));

        result.createObjectNode().put("result", res);
    }

    @Override
    protected void doDelete(JsonResult result, CallContext callContext) throws Exception {
        throw new NotSupportedException();
    }
}
