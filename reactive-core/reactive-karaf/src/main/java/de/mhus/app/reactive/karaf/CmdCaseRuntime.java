/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.reactive.karaf;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.engine.util.EngineUtil;
import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.app.reactive.model.engine.PNodeInfo;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pcase-runtime", description = "Case modifications - print all runtime information")
@Service
public class CmdCaseRuntime extends AbstractCmd {

    @Argument(
            index = 0,
            name = "id",
            required = true,
            description = "case id or custom id",
            multiValued = false)
    String caseId;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        PCase caze = EngineUtil.getCase(api.getEngine(), caseId);
        for (PNodeInfo node : api.getEngine().storageGetFlowNodes(caze.getId(), null)) {
            if (node.getType() == TYPE_NODE.RUNTIME) {
                System.out.println(">>> RUNTIME " + node.getId() + " " + node.getState());
                try {
                    PNode pRuntime = api.getEngine().getNodeWithoutLock(node.getId());
                    Util.printRuntime(api, caze, pRuntime, tblOpt);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        return null;
    }
}
