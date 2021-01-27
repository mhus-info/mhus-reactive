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
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pnode-assign", description = "Node modifications - assign a user task to a user")
@Service
public class CmdNodeAssign extends AbstractCmd {

    @Argument(
            index = 0,
            name = "id",
            required = true,
            description = "node id or custom id",
            multiValued = false)
    String nodeId;

    @Argument(
            index = 1,
            name = "user",
            required = true,
            description = "user id to assign to",
            multiValued = false)
    String userId;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        PNode node = EngineUtil.getFlowNode(api.getEngine(), nodeId);
        System.out.println("Update: " + node);
        api.getEngine().assignUserTask(node.getId(), userId);
        System.out.println("OK");

        return null;
    }
}
