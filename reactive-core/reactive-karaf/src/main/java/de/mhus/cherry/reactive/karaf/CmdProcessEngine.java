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
package de.mhus.cherry.reactive.karaf;

import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.engine.Engine.EngineCaseLock;
import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.osgi.IEngineAdmin;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pengine", description = "Engine modifiations")
@Service
public class CmdProcessEngine extends AbstractCmd {

    @Argument(
            index = 0,
            name = "cmd",
            required = true,
            description =
                    "Command:\n"
                            + " parameter [<key=value>*] - set engine configuration parameters. Will not be saved by default.\n"
                            + " fire external <nodeId> [<key=value>*]          - fire external event\n"
                            + " fire message <caseId> <message> [<key=value>*] - fire message to case or * for any case\n"
                            + " fire signal <signal> [<key=value>*]            - fire signal to the engine\n"
                            + " uninstall <name>        - uninstall process\n"
                            + " install [<path>*]       - install process, give pathes to jar files or 'classes' folders\n"
                            + " cleanup                 - execute engine cleanup\n"
                            + " step                    - execute engine next step\n"
                            + " save                    - save engine configuration\n"
                            + " load                    - load engine configuration\n"
                            + " status                  - print current engine status\n"
                            + " statistics              - print statistic informtion\n"
                            + " suspend                 - suspent automatic engine steps\n"
                            + " resume                  - resume automatic engine steps\n"
                            + " start                   - start engine\n"
                            + " stop                    - stop and destroy engine\n"
                            + " archive [<caseId>*]     - archive special cases or all (if no id is set)\n"
                            + " execute <uri>           - executes the uri, e.g. bpm://process/pool to start a case\n"
                            + " health\n"
                            + " locks                   - Print case locks (cluster lock)\n"
                            + " lock <case id>          - Get lock information\n"
                            + " unlock <case uuid>      - Unlock a case lock\n"
                            + " areas                   - Print restricted areas\n"
                            + " area.release <name>     - Release a restricted area\n"
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

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        if (cmd.equals("statistics")) {
            System.out.println("Started     : " + api.getStartDate());
            System.out.println("Status      : " + api.getEngineStatus());
            System.out.println("Rounds      : " + api.getEngine().getStatisticRounds());
            System.out.println("Case Closed : " + api.getEngine().getStatisticCaseClosed());
            System.out.println("Case Started: " + api.getEngine().getStatisticCaseStarted());
        } else
        if (cmd.equals("area.release")) {
            api.getEnginePersistence().set(EngineConst.AREA_PREFIX + parameters[0], null);
            System.out.println("OK");
        } else
        if (cmd.equals("areas")) {
            ConsoleTable table = new ConsoleTable(tblOpt);
            table.setHeaderValues("Area","LockId","State");
            for (Entry<String, String> entry : api.getEnginePersistence().getParameters().entrySet()) {
                if (entry.getKey().startsWith(EngineConst.AREA_PREFIX)) {
                    PNodeInfo runtime = api.getEngine().getFlowNodeInfo(UUID.fromString(entry.getValue()));
                    table.addRowValues(entry.getKey().substring(EngineConst.AREA_PREFIX.length()), entry.getValue(), runtime.getState());
                }
            }
            table.print();
        } else
        if (cmd.equals("lock")) {
            UUID id = UUID.fromString(parameters[0]);
            for (EngineCaseLock lock : api.getEngine().getCaseLocks()) {
                if (lock.getCaseId().equals(id)) {
                    System.out.println(lock.getStartStacktrace());
                    return null;
                }
            }
        } else if (cmd.equals("unlock")) {
            UUID id = UUID.fromString(parameters[0]);
            for (EngineCaseLock lock : api.getEngine().getCaseLocks()) {
                if (lock.getCaseId().equals(id)) {
                    lock.getLock().unlockHard();
                    System.out.println("UNLOCKED");
                    return null;
                }
            }
        } else if (cmd.equals("locks")) {
            ConsoleTable table = new ConsoleTable(tblOpt);
            table.setHeaderValues("Case Id", "Owner", "Time", "Lock");
            for (EngineCaseLock lock : api.getEngine().getCaseLocks()) {
                Lock l = lock.getLock();
                table.addRowValues(
                        lock.getCaseId(),
                        l == null ? null : l.getOwner(),
                        l == null ? null : new Date(l.getLockTime()),
                        l);
            }
            table.print();
        } else if (cmd.equals("execute")) {
            System.out.println(api.getEngine().doExecute(parameters[0]));
        } else if (cmd.equals("cleanup")) {
            IEngineAdmin uiApi = M.l(IEngineAdmin.class);
            uiApi.cleanupCache();
            System.out.println("OK");
        } else if (cmd.equals("fire")) {
            if (parameters[0].equals("external")) {
                MProperties p = new MProperties();
                for (int i = 2; i < parameters.length; i++) {
                    String parts = parameters[i];
                    String k = MString.beforeIndex(parts, '=');
                    String v = MString.afterIndex(parts, '=');
                    p.put(k, v);
                }
                api.getEngine().fireExternal(UUID.fromString(parameters[1]), null, p);
                System.out.println("OK");
            } else if (parameters[0].equals("message")) {
                MProperties p = new MProperties();
                for (int i = 3; i < parameters.length; i++) {
                    String parts = parameters[i];
                    String k = MString.beforeIndex(parts, '=');
                    String v = MString.afterIndex(parts, '=');
                    p.put(k, v);
                }
                if (parameters[1].equals("*")) api.getEngine().fireMessage(null, parameters[2], p);
                else api.getEngine().fireMessage(UUID.fromString(parameters[1]), parameters[2], p);
                System.out.println("OK");
            } else if (parameters[0].equals("signal")) {
                MProperties p = new MProperties();
                for (int i = 2; i < parameters.length; i++) {
                    String parts = parameters[i];
                    String k = MString.beforeIndex(parts, '=');
                    String v = MString.afterIndex(parts, '=');
                    p.put(k, v);
                }
                api.getEngine().fireSignal(parameters[1], p);
                System.out.println("OK");
            } else {
                System.out.println("Unknown type");
            }

        } else if (cmd.equals("uninstall")) {
            api.removeProcess(parameters[0]);
        } else if (cmd.equals("install")) {
            System.out.println(api.addProcess(parameters, true));
        } else if (cmd.equals("cleanup")) {
            api.getEngine().doCleanupCases();
            System.out.println("OK");
        } else if (cmd.equals("step")) {
            api.getEngine().doProcessNodes();
            System.out.println("OK");
        } else if (cmd.equals("parameters")) {
            PEngine persistent = api.getEnginePersistence();
            persistent.reload();
            if (parameters != null) {
                MProperties properties = MProperties.explodeToMProperties(parameters);
                for (Entry<String, Object> entry : properties.entrySet())
                    persistent.getParameters().put(entry.getKey(), String.valueOf(entry.getValue()));
                persistent.save();
            }
            System.out.println(persistent);
        } else if (cmd.equals("save")) {
            api.getEngine().saveEnginePersistence();
            System.out.println("OK");
        } else if (cmd.equals("load")) {
            api.getEngine().loadEnginePersistence();
            PEngine persistent = api.getEnginePersistence();
            System.out.println(persistent);
        } else if (cmd.equals("status")) {
            System.out.println(api.getEngineStatus());
        } else if (cmd.equals("suspend")) {
            api.setExecutionSuspended(true);
            System.out.println("OK");
        } else if (cmd.equals("resume")) {
            api.setExecutionSuspended(false);
            System.out.println("OK");
        } else if (cmd.equals("start")) {
            api.startEngine();
            System.out.println("OK");
        } else if (cmd.equals("stop")) {
            api.stopEngine();
            System.out.println("OK");
        } else if (cmd.equals("archive")) {
            if (parameters == null) {
                System.out.println("Archive all");
                api.getEngine().archiveAll();
            } else {
                for (String id : parameters) {
                    System.out.println("Archive: " + id);
                    api.getEngine().archiveCase(UUID.fromString(id));
                }
            }
        } else if (cmd.equals("health")) {
            int severe = 0;
            {
                SearchCriterias criterias = new SearchCriterias(new String[] {"state=severe"});

                ConsoleTable table = new ConsoleTable(tblOpt);
                table.setHeaderValues("Id", "CustomId", "Uri", "State", "Close");
                for (PCaseInfo info : api.getEngine().storageSearchCases(criterias)) {
                    PCase caze = api.getEngine().getCaseWithoutLock(info.getId());
                    table.addRowValues(
                            info.getId(),
                            caze.getCustomId(),
                            caze.getUri(),
                            caze.getState(),
                            caze.getClosedCode() + " " + caze.getClosedMessage());
                    severe++;
                }
                table.print(System.out);
            }
            {
                SearchCriterias criterias = new SearchCriterias(new String[] {"state=severe"});

                ConsoleTable table = new ConsoleTable(tblOpt);
                table.setHeaderValues(
                        "Id",
                        "Custom",
                        "Name",
                        "State",
                        "Type",
                        "Scheduled",
                        "CaseId",
                        "Assigned",
                        "Uri");
                for (PNodeInfo info : api.getEngine().storageSearchFlowNodes(criterias)) {
                    PNode node = api.getEngine().getNodeWithoutLock(info.getId());
                    String scheduled = "-";
                    Entry<String, Long> scheduledEntry = node.getNextScheduled();
                    if (scheduledEntry != null) {
                        long diff = scheduledEntry.getValue() - System.currentTimeMillis();
                        if (diff > 0) scheduled = MPeriod.getIntervalAsString(diff);
                    }
                    table.addRowValues(
                            node.getId(),
                            info.getCustomId(),
                            node.getName(),
                            node.getState(),
                            node.getType(),
                            scheduled,
                            node.getCaseId(),
                            node.getAssignedUser(),
                            info.getUri());
                    severe++;
                }
                table.print(System.out);
            }

            if (severe == 0) System.out.println("healthy");
            else System.out.println(severe + " in problems");

        } else {
            System.out.println("Unknown command");
        }

        return null;
    }
}
