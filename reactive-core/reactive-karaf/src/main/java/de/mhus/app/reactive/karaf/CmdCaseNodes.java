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

import java.util.Date;
import java.util.Map.Entry;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.engine.util.EngineUtil;
import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.app.reactive.model.engine.PNodeInfo;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pcase-nodes",
        description = "Case modifications - print case nodes")
@Service
public class CmdCaseNodes extends AbstractCmd {

    @Argument(
            index = 0,
            name = "id",
            required = true,
            description = "case id or custom id",
            multiValued = false)
    String caseId;

    @Option(name = "-a", aliases = "--all", description = "Print all", required = false)
    private boolean all;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        PCase caze = EngineUtil.getCase(api.getEngine(), caseId);
        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Id", "CName", "State", "Type", "Modified", "Scheduled");
        table.getColumn(0).minWidth = 32;
        for (PNodeInfo info : api.getEngine().storageGetFlowNodes(caze.getId(), null)) {
            if (all || info.getState() != STATE_NODE.CLOSED) {
                try {
                    PNode node = api.getEngine().getNodeWithoutLock(info.getId());
                    String scheduled = "-";
                    Entry<String, Long> scheduledEntry = node.getNextScheduled();
                    if (scheduledEntry != null) {
                        long diff = scheduledEntry.getValue() - System.currentTimeMillis();
                        if (diff > 0) scheduled = MPeriod.getIntervalAsString(diff);
                    }
                    table.addRowValues(
                            node.getId(),
                            node.getCanonicalName(),
                            node.getState(),
                            node.getType(),
                            new Date(info.getModified()),
                            scheduled);
                } catch (Throwable t) {
                    table.addRowValues(
                            info.getId(),
                            info.getCanonicalName(),
                            info.getState(),
                            info.getType(),
                            new Date(info.getModified()),
                            t.getMessage());
                }
            }
        }
        table.print(System.out);

        return null;
    }
}
