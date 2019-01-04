/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.karaf;

import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.engine.ui.UiProcess;
import de.mhus.cherry.reactive.engine.util.EngineUtil;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AUserTask;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.ui.INodeDescription;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.console.ConsoleTable;

@Command(scope = "reactive", name = "pnode", description = "Node modifiations")
@Service
public class CmdNode extends MLog implements Action {


	@Argument(index=0, name="cmd", required=true, description="Command:\n"
			+ " executing       - print currently executing nodes\n"
			+ " list [search: state=,name=,search=,index0..9=,uri=,case=]  - list all nodes\n"
			+ " view <id> [user] [lang] - view node details\n"
			+ " cancel <id>   - cancel node\n"
			+ " retry <id>    - set node back to running\n"
			+ " runtime <id>  - print runtime for this node\n"
			+ "", multiValued=false)
    String cmd;

	@Option(name="-a", aliases="--all", description="Print all",required=false)
	private boolean all;

	@Option(name="-f", aliases="--full", description="Print full table output",required=false)
	private boolean full;
	
	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;

	
	@Override
	public Object execute() throws Exception {

		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);

		if (cmd.equals("runtime")) {
			PNode node = api.getEngine().getFlowNode(UUID.fromString(parameters[0]));
			PCase caze = api.getEngine().getCase(node.getCaseId());
			EngineContext context = api.getEngine().createContext(caze, node);
			PNode pRuntime = api.getEngine().getRuntimeForPNode(context, node);
			System.out.println(">>> RUNTIME " + pRuntime.getId() + " " + pRuntime.getState());
			Util.printRuntime(api, caze, pRuntime, full);
		} else
		if (cmd.equals("submit")) {
			MProperties p = new MProperties();
			for (int i = 1; i < parameters.length; i++) {
				String parts = parameters[i];
				String k = MString.beforeIndex(parts, '=');
				String v = MString.afterIndex(parts, '=');
				p.put(k, v);
			}
			api.getEngine().submitUserTask(UUID.fromString(parameters[0]), p);
			System.out.println("OK");
		} else
		if (cmd.equals("unassign")) {
			api.getEngine().unassignUserTask(UUID.fromString(parameters[0]));
			System.out.println("OK");
		} else
		if (cmd.equals("assign")) {
			api.getEngine().assignUserTask(UUID.fromString(parameters[0]), parameters[1]);
			System.out.println("OK");
		} else
		if (cmd.equals("resave")) {
			api.getEngine().resaveFlowNode(UUID.fromString(parameters[0]));
			System.out.println("OK");
		} else
		if (cmd.equals("executing")) {
			
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Id","Case","Name","Time","State","Type","CaseId");
			for (UUID nodeId : api.getEngine().getExecuting()) {
				PNode node = api.getEngine().getFlowNode(nodeId);
				PCase caze = api.getEngine().getCase(node.getCaseId());
				String time = MPeriod.getIntervalAsString(System.currentTimeMillis() - node.getLastRunDate());
				table.addRowValues(node.getId(),caze.getName(), node.getName(),time,node.getState(),node.getType(), node.getCaseId());
			}
			table.print(System.out);

		} else
		if (cmd.equals("values")) {
			SearchCriterias criterias = new SearchCriterias(parameters);
			
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Id","0","1","2","3","4","5","6","7","8","9");
			for (PNodeInfo info : api.getEngine().storageSearchFlowNodes(criterias)) {
				if (all || (info.getState() != STATE_NODE.CLOSED && info.getType() != TYPE_NODE.RUNTIME) ) {
					table.addRowValues(
							info.getId(),
							info.getIndexValue(0),
							info.getIndexValue(1),
							info.getIndexValue(2),
							info.getIndexValue(3),
							info.getIndexValue(4),
							info.getIndexValue(5),
							info.getIndexValue(6),
							info.getIndexValue(7),
							info.getIndexValue(8),
							info.getIndexValue(9)
							);
					}
				}
				table.print(System.out);
			} else
			if (cmd.equals("list")) {
			SearchCriterias criterias = new SearchCriterias(parameters);
			
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Id","Custom","Name","State","Type","Modified","Scheduled","CaseId","Assigned","Uri");
			table.getColumn(0).minWidth = 32;
			table.getColumn(7).minWidth = 32;
			for (PNodeInfo info : api.getEngine().storageSearchFlowNodes(criterias)) {
				PNode node = api.getEngine().getFlowNode(info.getId());
				if (all || (node.getState() != STATE_NODE.CLOSED && node.getType() != TYPE_NODE.RUNTIME) ) {
					String scheduled = "-";
					Entry<String, Long> scheduledEntry = node.getNextScheduled();
					if (scheduledEntry != null) {
						long diff = scheduledEntry.getValue() - System.currentTimeMillis();
						if (diff > 0)
							scheduled = MPeriod.getIntervalAsString(diff);
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
							info.getUri()
							);
				}
			}
			table.print(System.out);
		} else
		if (cmd.equals("view")) {
			PNode node = EngineUtil.getFlowNode(api.getEngine(),parameters[0]);
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
				if (diff > 0)
					scheduled = MPeriod.getIntervalAsString(diff);
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
				System.out.println("Form:\n" + ((AUserTask<?>)aNode).getForm().build());
				System.out.println("\nValues:\n" + ((AUserTask<?>)aNode).getFormValues());
			}
			
			if (parameters.length > 1) {
				String user = parameters[1];
				Locale locale = null;
				if (parameters.length > 2)
					locale = Locale.forLanguageTag(parameters[2]);
				IEngineFactory uiFactory = MApi.lookup(IEngineFactory.class);
				IEngine engine = uiFactory.create(user, locale);
				INode inode = engine.getNode(node.getId().toString(), new String[]{"*"});
				INodeDescription idesc = engine.getNodeDescription(inode);
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
					for (Entry<String, Object> entry : new TreeMap<String,Object>(((UiProcess)engine.getProcess(inode.getUri())).getProperties()).entrySet())
						System.out.println(entry.getKey() + "=" + entry.getValue());
			}

			
		} else
		if (cmd.equals("cancel")) {
			for (String id : parameters) {
				System.out.println("Cancel: " + id);
				api.getEngine().cancelFlowNode(UUID.fromString(id));
			}
		} else
		if (cmd.equals("retry")) {
			for (String id : parameters) {
				System.out.println("Retry: " + id);
				api.getEngine().retryFlowNode(UUID.fromString(id));
			}
		} else {
			System.out.println("Unknown command");
		}
		
		return null;
	}

}
