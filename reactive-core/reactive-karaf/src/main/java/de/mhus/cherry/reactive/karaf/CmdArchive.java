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
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "parchive", description = "Archive data lookup")
@Service
public class CmdArchive extends AbstractCmd {

	@Argument(index=0, name="cmd", required=true, description="Command:\n"
			+ " cases <search>\n"
			+ " nodes <serach>\n"
			+ " case <id>\n"
			+ " node <id>\n"
			+ "", multiValued=false)
    String cmd;

	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
	String[] parameters;
	
	@Override
	public Object execute2() throws Exception {

		ReactiveAdmin api = M.l(ReactiveAdmin.class);
		Engine engine = api.getEngine();
		
		if (cmd.equals("node")) {
			PNode node = engine.archiveLoadFlowNode(UUID.fromString(parameters[0]));
			
			System.out.println("Name      : " + node.getName());
			System.out.println("Id        : " + node.getId());
			System.out.println("State     : " + node.getState());
			System.out.println("CName     : " + node.getCanonicalName());
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

			System.out.println();
			for (Entry<String, Object> entry : node.getParameters().entrySet())
				System.out.println("  " + entry.getKey() + "=" + entry.getValue());
			
		} else
		if (cmd.equalsIgnoreCase("nodes")) {
			SearchCriterias criterias = new SearchCriterias(parameters);
			Result<PNodeInfo> res = engine.archiveSearchFlowNodes(criterias);
			
			ConsoleTable table = new ConsoleTable(tableAll, tblOpt);
			table.setHeaderValues("Id","Custom","Name","State","Type","CaseId","Assigned","Uri");

			for (PNodeInfo info : res) {
				PNode node = api.getEngine().getFlowNode(info.getId());
				table.addRowValues(
						node.getId(),
						info.getCustomId(), 
						node.getName(),
						node.getState(),
						node.getType(), 
						node.getCaseId(),
						node.getAssignedUser(),
						info.getUri()
						);
			}
			table.print(System.out);
		} else
		if (cmd.equals("case")) {
			PCase caze = engine.archiveLoadCase(UUID.fromString(parameters[0]));
			
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
			System.out.println("Scheduled : " + (caze.getScheduled() > 0 ? MPeriod.getIntervalAsString(caze.getScheduled() - System.currentTimeMillis()) : "-"));
			System.out.println("Close     : " + caze.getClosedCode() + " " + caze.getClosedMessage());
			System.out.println("Options   : " + caze.getOptions());
			if (caze.getParameters() != null) {
				System.out.println();
				for (Entry<String, Object> entry : caze.getParameters().entrySet()) {
					System.out.println("  " + entry.getKey() + "=" + entry.getValue());
				}
			}
			
		} else
		if (cmd.equals("cases")) {
			SearchCriterias criterias = new SearchCriterias(parameters);
			Result<PCaseInfo> res = engine.archiveSearchCases(criterias);

			ConsoleTable table = new ConsoleTable(tableAll, tblOpt);
			table.setHeaderValues("Id","CustomId","Uri","State","Close");
			for (PCaseInfo info : res) {
				PCase caze = api.getEngine().getCase(info.getId());
				table.addRowValues(info.getId(), caze.getCustomId(), caze.getUri(), caze.getState(), caze.getClosedCode() + " " + caze.getClosedMessage() );
			}
			table.print(System.out);

		} else {
			System.out.println("Unknown command");
		}
		
		return null;
	}

}
