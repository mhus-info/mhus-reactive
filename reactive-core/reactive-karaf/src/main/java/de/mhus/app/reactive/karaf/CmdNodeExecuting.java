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

import java.util.UUID;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pnode-executing",
        description = "Node modifications - print currently executing nodes")
@Service
public class CmdNodeExecuting extends AbstractCmd {

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Id", "Case", "Name", "Time", "State", "Type", "CaseId");
        for (UUID nodeId : api.getEngine().getExecuting()) {
            PNode node = api.getEngine().getNodeWithoutLock(nodeId);
            PCase caze = api.getEngine().getCaseWithoutLock(node.getCaseId());
            String time =
                    MPeriod.getIntervalAsString(System.currentTimeMillis() - node.getLastRunDate());
            table.addRowValues(
                    node.getId(),
                    caze.getName(),
                    node.getName(),
                    time,
                    node.getState(),
                    node.getType(),
                    node.getCaseId());
        }
        table.print(System.out);

        return null;
    }
}
