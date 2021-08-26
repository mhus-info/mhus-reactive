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
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.engine.util.EngineUtil;
import de.mhus.app.reactive.model.activity.AElement;
import de.mhus.app.reactive.model.activity.AUserTask;
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.app.reactive.model.engine.PNodeInfo;
import de.mhus.app.reactive.model.ui.IEngine;
import de.mhus.app.reactive.model.ui.IEngineFactory;
import de.mhus.app.reactive.model.ui.INode;
import de.mhus.app.reactive.model.ui.INodeDescription;
import de.mhus.app.reactive.model.uimp.UiProcess;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MPeriod;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pnode-view",
        description = "Node modifications - view node details")
@Service
public class CmdNodeView extends AbstractCmd {

    @Argument(
            index = 0,
            name = "id",
            required = true,
            description = "node id or custom id",
            multiValued = false)
    String nodeId;

    @Argument(index = 1, name = "user", required = false, description = "user", multiValued = false)
    String userId;

    @Argument(
            index = 2,
            name = "language",
            required = false,
            description = "Language",
            multiValued = false)
    String language;

    @Option(name = "-a", aliases = "--all", description = "Print all", required = false)
    private boolean all;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        PNode node = EngineUtil.getFlowNode(api.getEngine(), nodeId);
        PNodeInfo info = api.getEngine().getFlowNodeInfo(node.getId());

        System.out.println("Name      : " + node.getName());
        System.out.println("Id        : " + node.getId());
        System.out.println("State     : " + node.getState());
        System.out.println("CName     : " + node.getCanonicalName());
        System.out.println("Uri       : " + info.getUri());
        System.out.println("CustomId  : " + info.getCustomId());
        System.out.println("CustomerId: " + info.getCustomerId());
        System.out.println("Created   : " + MDate.toIso8601(new Date(node.getCreationDate())));
        String scheduled = "-";
        Entry<String, Long> scheduledEntry = node.getNextScheduled();
        if (scheduledEntry != null) {
            long diff = scheduledEntry.getValue() - System.currentTimeMillis();
            if (diff > 0) scheduled = MPeriod.getIntervalAsString(diff);
        }
        System.out.println("Scheduled : " + scheduled);
        System.out.println("Type      : " + node.getType());
        System.out.println("StartState: " + node.getStartState());
        System.out.println("Suspended : " + node.getSuspendedState());
        System.out.println("Signals   : " + node.getSignalTriggers());
        System.out.println("Messages  : " + node.getMessageTriggers());
        System.out.println("LastRun   : " + MDate.toIso8601(new Date(node.getLastRunDate())));
        System.out.println("Assigned  : " + node.getAssignedUser());
        System.out.println("ExitMsg   : " + node.getExitMessage());
        System.out.println("TryCount  : " + node.getTryCount());
        System.out.println("CaseId    : " + node.getCaseId());
        System.out.println("RuntimeId : " + node.getRuntimeId());
        System.out.println("NextScheduled: " + node.getNextScheduled());
        System.out.println("MessageList: " + node.getMessagesAsString());
        System.out.println("SignalList : " + node.getSignalsAsString());
        System.out.println("Message    : " + node.getMessage());
        String[] values = node.getIndexValues();
        if (values != null) {
            for (int i = 0; i < values.length; i++)
                System.out.println("Value " + i + ": " + values[i]);
        } else {
            System.out.println("Values are empty");
        }

        System.out.println();
        for (Entry<String, Object> entry : node.getParameters().entrySet())
            System.out.println("  " + entry.getKey() + "=" + entry.getValue());

        if (node.getType() == TYPE_NODE.USER) {
            System.out.println();
            AElement<?> aNode = api.getEngine().getANode(node.getId());
            System.out.println("Form:\n" + ((AUserTask<?>) aNode).getForm().build());
            System.out.println("\nValues:\n" + ((AUserTask<?>) aNode).getFormValues());
        }

        if (userId != null) {
            Locale locale = null;
            if (language != null) locale = Locale.forLanguageTag(language);
            IEngineFactory uiFactory = M.l(IEngineFactory.class);
            IEngine engine = uiFactory.create(userId, locale);
            INode inode = engine.getNode(node.getId().toString(), new String[] {"*"});
            INodeDescription idesc = engine.getNodeDescription2(inode);
            System.out.println();
            System.out.println("User        : " + engine.getUser());
            System.out.println("Locale      : " + engine.getLocale());
            System.out.println("Display name: " + idesc.getDisplayName());
            System.out.println("Description : " + idesc.getDescription());
            System.out.println();
            for (Entry<String, String> entry : inode.getProperties().entrySet()) {
                String name = idesc.getPropertyName(entry.getKey());
                System.out.println(name + "=" + entry.getValue());
            }
            if (all)
                for (Entry<String, Object> entry :
                        new TreeMap<String, Object>(
                                        ((UiProcess) engine.getProcess(inode.getUri()))
                                                .getProperties())
                                .entrySet())
                    System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        return null;
    }
}
