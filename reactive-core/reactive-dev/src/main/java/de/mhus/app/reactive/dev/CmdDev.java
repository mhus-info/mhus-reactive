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
package de.mhus.app.reactive.dev;

import java.util.Date;
import java.util.List;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.engine.Engine.EngineCaseLock;
import de.mhus.app.reactive.engine.util.EngineUtil;
import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.engine.PCaseInfo;
import de.mhus.app.reactive.model.engine.PCaseLock;
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.model.engine.PNodeInfo;
import de.mhus.app.reactive.model.engine.SearchCriterias;
import de.mhus.app.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.app.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.app.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pdev", description = "reactive development tool")
@Service
public class CmdDev extends AbstractCmd {

    @Argument(
            index = 0,
            name = "cmd",
            required = true,
            description =
                    "Command:\n"
                            + " cancelallcases [criterias] - cancel all cases\n"
                            + " cancelallnodes [criterias] - cancel all nodes\n"
                            + " locks print locks details\n"
                            + "",
            multiValued = false)
    String cmd;

    @Argument(
            index = 1,
            name = "parameters",
            required = false,
            description = "Parameters",
            multiValued = true)
    String[] parameters;

    @Option(name = "-a", aliases = "--all", description = "Print all", required = false)
    private boolean all;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);
        Console console = M.l(Console.class);

        if (cmd.equals("statistics")) {
            SearchCriterias criterias = new SearchCriterias();
            long nodesActive = 0;
            long nodesAll = 0;
            long nodesClosed = 0;

            ConsoleTable table = new ConsoleTable(tblOpt);
            table.setHeaderValues("Id", "Custom", "Name", "State", "Type", "Modified", "CaseId");

            for (PNodeInfo info : api.getEngine().storageSearchFlowNodes(criterias)) {
                if (info.getState() != STATE_NODE.CLOSED && info.getType() != TYPE_NODE.RUNTIME) {
                    if (table.size() < console.getHeight() - 15)
                        table.addRowValues(
                                info.getId(),
                                info.getCustomId(),
                                info.getCanonicalName(),
                                info.getState(),
                                info.getType(),
                                new Date(info.getModified()),
                                info.getCaseId());
                    nodesActive++;
                }
                if (info.getState() == STATE_NODE.CLOSED) nodesClosed++;
                nodesAll++;
            }

            long casesActive = 0;
            long casesClosed = 0;
            long casesAll = 0;
            for (PCaseInfo info : api.getEngine().storageSearchCases(criterias)) {
                if (info.getState() != STATE_CASE.CLOSED) {
                    casesActive++;
                } else casesClosed++;
                casesAll++;
            }

            long locksOlder5 = 0;
            long now = System.currentTimeMillis();
            List<EngineCaseLock> locks = api.getEngine().getCaseLocks();
            for (EngineCaseLock lock : locks)
                if (now - lock.getLock().getLockTime() > MPeriod.MINUTE_IN_MILLISECONDS * 5)
                    locksOlder5++;

            System.out.println(new Date());
            table.print();
            System.out.println("Cases All         : " + casesAll);
            System.out.println("Cases Active      : " + casesActive);
            System.out.println("Cases Closed      : " + casesClosed);
            System.out.println("Nodes All         : " + nodesAll);
            System.out.println("Nodes Active      : " + nodesActive);
            System.out.println("Nodes Closed      : " + nodesClosed);
            System.out.println("Cases Local Closed: " + api.getEngine().getStatisticCaseClosed());
            System.out.println(
                    "Locks Local       : "
                            + locks.size()
                            + " ("
                            + locksOlder5
                            + " older 5 Minutes)");

        } else if (cmd.equals("locks")) {
            System.out.println("Locks:");
            for (EngineCaseLock lock : api.getEngine().getCaseLocks()) {
                try {
                    Lock systemLock = lock.getLock();
                    System.out.println(
                            lock.getCaseId()
                                    + " "
                                    + systemLock.isLocked()
                                    + " "
                                    + MPeriod.getIntervalAsString(
                                            System.currentTimeMillis() - systemLock.getLockTime()));
                    System.out.println("   Current Thread: " + lock.getOwnerThreadId());
                    System.out.println("   " + systemLock.getStartStackTrace());
                } catch (Throwable t) {
                }
            }

        } else if (cmd.equals("cancelallcases")) {

            SearchCriterias criterias = new SearchCriterias(parameters);
            for (PCaseInfo info : api.getEngine().storageSearchCases(criterias)) {
                if (info.getState() != STATE_CASE.CLOSED) {
                    try (PCaseLock lock =
                            EngineUtil.getCaseLock(
                                    api.getEngine(),
                                    info.getId().toString(),
                                    "cmddev.cancelallcases")) {
                        PCase caze = lock.getCase();
                        System.out.println("Cancel: " + caze);
                        lock.closeCase(true, -1, "cancelled by cmd");
                    } catch (Throwable t) {
                        System.out.println("Error in " + info);
                        t.printStackTrace();
                    }
                }
            }
        } else if (cmd.equals("cancelallnodes")) {
            SearchCriterias criterias = new SearchCriterias(parameters);
            for (PNodeInfo info : api.getEngine().storageSearchFlowNodes(criterias)) {
                if (info.getState() != STATE_NODE.CLOSED) {
                    try (PCaseLock lock =
                            EngineUtil.getCaseLock(
                                    api.getEngine(),
                                    info.getCaseId().toString(),
                                    "cmddev.cancelallnodes")) {
                        PNode node = lock.getFlowNode(info.getId());
                        System.out.println("Cancel: " + node);
                        lock.closeFlowNode(null, node, STATE_NODE.CLOSED);
                    } catch (Throwable t) {
                        System.out.println("Error in " + info);
                        t.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Unknown command");
        }

        return null;
    }
}
