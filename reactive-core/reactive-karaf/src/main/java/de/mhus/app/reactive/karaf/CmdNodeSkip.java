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
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.engine.EngineContext;
import de.mhus.app.reactive.engine.util.EngineUtil;
import de.mhus.app.reactive.model.activity.AActivity;
import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.engine.PCaseLock;
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.app.reactive.model.util.ActivityUtil;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pnode-skip",
        description = "Node modifications - skip a node and start the next one")
@Service
public class CmdNodeSkip extends AbstractCmd {

    @Argument(
            index = 0,
            name = "id",
            required = true,
            description = "node id or custom id",
            multiValued = true)
    String[] nodeId;

    @Option(name = "-n", aliases = "--next", description = "next step", required = false)
    private String nextName = "";

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        for (String id : nodeId) {
            PNode node = EngineUtil.getFlowNode(api.getEngine(), id);
            System.out.println("Skip: " + node);

            try (PCaseLock lock = api.getEngine().getCaseLock(node.getCaseId(), "cmdnode.skip")) {

                PCase caze = api.getEngine().getCaseWithoutLock(node.getCaseId());
                node = lock.getFlowNode(node.getId()); // reload node
                EngineContext context = api.getEngine().createContext(lock, caze, node);
                AActivity<?> aNode = context.getANode();

                Class<? extends AActivity<?>> next = ActivityUtil.getOutputByName(aNode, nextName);
                if (next == null) {
                    System.out.println(
                            "Output Activity not found: "
                                    + nextName
                                    + " in "
                                    + getClass().getCanonicalName());
                    return null;
                }
                // create new node
                context.createActivity(next);

                // close old
                node.setState(STATE_NODE.CLOSED);
                lock.saveFlowNode(node);
            }
        }

        return null;
    }
}
