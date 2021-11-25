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

import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.app.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.app.reactive.model.engine.PNodeInfo;
import de.mhus.app.reactive.model.engine.SearchCriterias;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pnode-list",
        description = "Node modifications - list all nodes")
@Service
public class CmdNodeList extends AbstractCmd {

    @Option(name = "-a", aliases = "--all", description = "Print all", required = false)
    private boolean all;

    @Option(name = "-1", aliases = "--one", description = "Print in one table", required = false)
    private boolean one;

    @Option(name = "-c", aliases = "--count", description = "Print count only", required = false)
    private boolean count;

    @Option(name = "-l", aliases = "--limit", description = "Limit", required = false)
    private int limit = 0;

    @Argument(
            index = 0,
            name = "search",
            required = false,
            description = "Search state=,name=,search=,index0..9=,uri=,case=",
            multiValued = true)
    String[] search;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        SearchCriterias criterias = new SearchCriterias(search);

        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues(
                "Id",
                "Custom",
                "Name",
                "State",
                "Type",
                "Modified",
                "Scheduled",
                "CaseId",
                "Assigned",
                "Uri");
        table.getColumn(0).minWidth = 32;
        table.getColumn(7).minWidth = 32;
        int c = 0;
        for (PNodeInfo info : api.getEngine().storageSearchFlowNodes(criterias)) {
            if (all
                    || (info.getState() != STATE_NODE.CLOSED
                            && info.getType() != TYPE_NODE.RUNTIME)) {
                c++;
                try {
                    if (count) {
                    } else {
                        PNode node = api.getEngine().getNodeWithoutLock(info.getId());
                        String scheduled = "-";
                        Entry<String, Long> scheduledEntry = node.getNextScheduled();
                        if (scheduledEntry != null) {
                            scheduled = Util.toPeriod(scheduledEntry.getValue());
                        }
                        table.addRowValues(
                                node.getId(),
                                info.getCustomId(),
                                node.getName(),
                                node.getState(),
                                node.getType(),
                                new Date(info.getModified()),
                                scheduled,
                                node.getCaseId(),
                                node.getAssignedUser(),
                                info.getUri());
                    }
                } catch (Throwable t) {
                    table.addRowValues(
                            info.getId(),
                            info.getCustomId(),
                            info.getCanonicalName(),
                            info.getState(),
                            info.getType(),
                            new Date(info.getModified()),
                            "?",
                            info.getCaseId(),
                            t.getMessage(),
                            info.getUri());
                }
            }
            if (!count && limit > 0 && c >= limit) {
                break;
            }
            if (!count && !one && table.size() >= 100) {
                table.print(System.out);
                table.clear();
                System.out.println();
            }
        }
        if (count) System.out.println(c);
        else if (table.size() > 0) table.print(System.out);

        return null;
    }
}
