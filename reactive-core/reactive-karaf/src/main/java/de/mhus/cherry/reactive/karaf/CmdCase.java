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

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.engine.util.EngineUtil;
import de.mhus.cherry.reactive.engine.util.PCaseLock;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.ICaseDescription;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.model.uimp.UiProcess;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pcase", description = "Case modifiations")
@Service
public class CmdCase extends AbstractCmd {

    @Argument(
            index = 0,
            name = "cmd",
            required = true,
            description =
                    "Command:\n"
                            + " view <id> [user] [lang ]- view case details\n"
                            + " nodes <id>       - print case bound nodes\n"
                            + " list [search: state=,name=,search=,index0..9=,uri=] - list all cases\n"
                            + " resume <id>*     - resume case\n"
                            + " suspend <id>*    - suspend case\n"
                            + " archive [id]*    - archive case or all if id is not set\n"
                            + " cancel <id>*     - cancel hard\n"
                            // + " locked           - print locked cases\n"
                            + " runtime <id>     - print all runtime information\n"
                            + " setoption <id> [key=value]*\n"
                            + " setparam <id> [key=value]*\n"
                            + "Experimental:\n"
                            + " updatefull <id>   - update case and nodes in database\n"
                            + " resave <id>      - load and save the data of the case again\n"
                            + " erase <uuid>     - erase a case data\n"
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

        if (cmd.equals("updatefull")) {
            try (PCaseLock lock = EngineUtil.getCaseLock(api.getEngine(), parameters[0])) {
                PCase caze = lock.getCase();
                System.out.println("CASE " + caze.getId());
                api.getEngine().storageUpdateFull(caze);
                for (PNodeInfo info : api.getEngine().storageGetFlowNodes(caze.getId(), null)) {
                    PNode node = api.getEngine().getNodeWithoutLock(info.getId());
                    System.out.println("NODE " + node.getId());
                    api.getEngine().storageUpdateFull(node);
                }
                System.out.println("UPDATED");
            }
        } else if (cmd.equals("setoption")) {

            try (PCaseLock lock = EngineUtil.getCaseLock(api.getEngine(), parameters[0])) {
                PCase caze = lock.getCase();
                Field field = caze.getClass().getDeclaredField("options");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<String, Object> p = (Map<String, Object>) field.get(caze);
                for (int i = 1; i < parameters.length; i++) {
                    String x = parameters[i];
                    MProperties.appendToMap(p, x);
                }
                lock.savePCase(null, false);
                api.getEngine().storageUpdateFull(caze);
                System.out.println("SAVED");
            }
        } else if (cmd.equals("setparam")) {
            try (PCaseLock lock = EngineUtil.getCaseLock(api.getEngine(), parameters[0])) {
                PCase caze = lock.getCase();
                Map<String, Object> p = caze.getParameters();
                for (int i = 1; i < parameters.length; i++) {
                    String x = parameters[i];
                    MProperties.appendToMap(p, x);
                }
                lock.savePCase(null, false);
                System.out.println("SAVED");
            }
        } else if (cmd.equals("runtime")) {
            PCase caze = EngineUtil.getCase(api.getEngine(), parameters[0]);
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
        } else
        //		if (cmd.equals("locked")) {
        //			ConsoleTable table = new ConsoleTable(tblOpt);
        //			table.setHeaderValues("Id","CustomId","Uri","State","Close");
        //			for (UUID id : api.getEngine().getLockedCases()) {
        //				PCase caze = api.getEngine().getCase(id);
        //				table.addRowValues(caze.getId(), caze.getCustomId(), caze.getUri(), caze.getState(),
        // caze.getClosedCode() + " " + caze.getClosedMessage() );
        //			}
        //			table.print(System.out);
        //		} else
        if (cmd.equals("resave")) {
            PCase caze = EngineUtil.getCase(api.getEngine(), parameters[0]);
            api.getEngine().resaveCase(caze.getId());
        } else if (cmd.equals("view")) {
            PCase caze = EngineUtil.getCase(api.getEngine(), parameters[0]);
            System.out.println("Uri       : " + caze.getUri());
            System.out.println("CustomId  : " + caze.getCustomId());
            System.out.println("CustomerId: " + caze.getCustomerId());
            System.out.println("Name      : " + caze.getName());
            System.out.println("Id        : " + caze.getId());
            System.out.println("State     : " + caze.getState());
            System.out.println("Milestone : " + caze.getMilestone());
            System.out.println("CName     : " + caze.getCanonicalName());
            System.out.println("CreatedBy : " + caze.getCreatedBy());
            System.out.println("Created   : " + MDate.toIso8601(new Date(caze.getCreationDate())));
            System.out.println(
                    "Scheduled : "
                            + (caze.getScheduled() > 0
                                    ? MPeriod.getIntervalAsString(
                                            caze.getScheduled() - System.currentTimeMillis())
                                    : "-"));
            System.out.println(
                    "Close     : " + caze.getClosedCode() + " " + caze.getClosedMessage());
            System.out.println("Options   : " + caze.getOptions());
            String[] values = caze.getIndexValues();
            if (values != null) {
                for (int i = 0; i < values.length; i++)
                    System.out.println("Value " + i + ": " + values[i]);
            } else {
                System.out.println("Values are empty");
            }
            if (caze.getParameters() != null) {
                System.out.println();
                for (Entry<String, Object> entry : caze.getParameters().entrySet()) {
                    System.out.println("  " + entry.getKey() + "=" + entry.getValue());
                }
            }

            if (parameters.length > 1) {
                String user = parameters[1];
                Locale locale = null;
                if (parameters.length > 2) locale = Locale.forLanguageTag(parameters[2]);
                IEngineFactory uiFactory = M.l(IEngineFactory.class);
                IEngine engine = uiFactory.create(user, locale);
                ICase icase = engine.getCase(caze.getId().toString(), new String[] {"*"});
                ICaseDescription idesc = engine.getCaseDescription2(icase);
                System.out.println();
                System.out.println("User        : " + engine.getUser());
                System.out.println("Locale      : " + engine.getLocale());
                System.out.println("Display name: " + idesc.getDisplayName());
                System.out.println("Description : " + idesc.getDescription());
                for (Entry<String, String> entry : icase.getProperties().entrySet()) {
                    String name = idesc.getPropertyName(entry.getKey());
                    System.out.println(name + "=" + entry.getValue());
                }
                if (all)
                    for (Entry<String, Object> entry :
                            new TreeMap<String, Object>(
                                            ((UiProcess) engine.getProcess(icase.getUri()))
                                                    .getProperties())
                                    .entrySet())
                        System.out.println(entry.getKey() + "=" + entry.getValue());
            }

        } else if (cmd.equals("nodes")) {
            PCase caze = EngineUtil.getCase(api.getEngine(), parameters[0]);
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
        } else if (cmd.equals("list")) {
            SearchCriterias criterias = new SearchCriterias(parameters);

            ConsoleTable table = new ConsoleTable(tblOpt);
            table.setHeaderValues(
                    "Id", "CustomId", "Customer", "Modified", "Uri", "State", "Close");
            table.getColumn(0).minWidth = 32;
            for (PCaseInfo info : api.getEngine().storageSearchCases(criterias)) {
                if (all || info.getState() != STATE_CASE.CLOSED) {
                    try {
                        PCase caze = api.getEngine().getCaseWithoutLock(info.getId());
                        table.addRowValues(
                                info.getId(),
                                caze.getCustomId(),
                                caze.getCustomerId(),
                                new Date(info.getModified()),
                                caze.getUri(),
                                caze.getState(),
                                caze.getClosedCode() + " " + caze.getClosedMessage());
                    } catch (Throwable t) {
                        table.addRowValues(
                                info.getId(),
                                info.getCustomId(),
                                info.getCustomerId(),
                                new Date(info.getModified()),
                                info.getUri(),
                                info.getState(),
                                t.getMessage());
                    }
                }
            }
            table.print(System.out);
        } else if (cmd.equals("resume")) {
            for (String id : parameters) {
                try {
                    PCase caze = EngineUtil.getCase(api.getEngine(), id);
                    System.out.println("Resume: " + caze);
                    api.getEngine().resumeCase(caze.getId());
                } catch (Throwable t) {
                    System.out.println("Error in " + id);
                    t.printStackTrace();
                }
            }
        } else if (cmd.equals("suspend")) {
            for (String id : parameters) {
                try {
                    PCase caze = EngineUtil.getCase(api.getEngine(), id);
                    System.out.println("Suspend: " + caze);
                    api.getEngine().suspendCase(caze.getId());
                } catch (Throwable t) {
                    System.out.println("Error in " + id);
                    t.printStackTrace();
                }
            }
        } else if (cmd.equals("archive")) {
            if (parameters == null) {
                System.out.println("Archive all");
                api.getEngine().archiveAll();
            } else {
                for (String id : parameters) {
                    try {
                        PCase caze = EngineUtil.getCase(api.getEngine(), id);
                        System.out.println("Archive: " + caze);
                        api.getEngine().archiveCase(caze.getId());
                    } catch (Throwable t) {
                        System.out.println("Error in " + id);
                        t.printStackTrace();
                    }
                }
            }
        } else if (cmd.equals("erase")) {
            System.out.println("Erase: " + parameters[0]);
            api.getEngine().storageDeleteCaseAndFlowNodes(UUID.fromString(parameters[0]));
        } else if (cmd.equals("cancel")) {
            for (String id : parameters) {
                try (PCaseLock lock = EngineUtil.getCaseLock(api.getEngine(), parameters[0])) {
                    PCase caze = lock.getCase();
                    System.out.println("Cancel: " + caze);
                    lock.closeCase(true, -1, "cancelled by cmd");
                } catch (Throwable t) {
                    System.out.println("Error in " + id);
                    t.printStackTrace();
                }
            }
        } else {
            System.out.println("Unknown command");
        }

        return null;
    }
}
