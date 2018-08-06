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

import de.mhus.cherry.reactive.engine.ui.UiProcess;
import de.mhus.cherry.reactive.engine.util.EngineUtil;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.ICaseDescription;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.core.console.ConsoleTable;

@Command(scope = "reactive", name = "pcase", description = "Case modifiations")
@Service
public class CmdCase extends MLog implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command:\n"
			+ " view <id> [user] [lang ]- view case details\n"
			+ " nodes <id>       - print case bound nodes\n"
			+ " list [search: state=,name=,search=,index0..9=,uri=] - list all cases\n"
			+ " resume <id>      - resume case\n"
			+ " suspend <id>     - suspend case\n"
			+ " archive <id>     - archive case\n"
			+ " cancel <id>      - cancel hard\n"
			+ " locked           - print locked cases"
			+ "", multiValued=false)
    String cmd;

	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
	String[] parameters;
	
	@Option(name="-a", aliases="--all", description="Print all",required=false)
	private boolean all;

	@Option(name="-f", aliases="--full", description="Print full table output",required=false)
	private boolean full;


	
	@Override
	public Object execute() throws Exception {

		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		
		if (cmd.equals("locked")) {
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Id","CustomId","Uri","State","Close");
			for (UUID id : api.getEngine().getLockedCases()) {
				PCase caze = api.getEngine().getCase(id);
				table.addRowValues(caze.getId(), caze.getCustomId(), caze.getUri(), caze.getState(), caze.getClosedCode() + " " + caze.getClosedMessage() );
			}
			table.print(System.out);
		} else
		if (cmd.equals("resave")) {
			api.getEngine().resaveCase(UUID.fromString(parameters[0]));
		} else
		if (cmd.equals("view")) {
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
			System.out.println("Scheduled : " + (caze.getScheduled() > 0 ? MTimeInterval.getIntervalAsString(caze.getScheduled() - System.currentTimeMillis()) : "-"));
			System.out.println("Close     : " + caze.getClosedCode() + " " + caze.getClosedMessage());
			System.out.println("Options   : " + caze.getOptions());
			if (caze.getParameters() != null) {
				System.out.println();
				for (Entry<String, Object> entry : caze.getParameters().entrySet()) {
					System.out.println("  " + entry.getKey() + "=" + entry.getValue());
				}
			}
			
			if (parameters.length > 1) {
				String user = parameters[1];
				Locale locale = null;
				if (parameters.length > 2)
					locale = Locale.forLanguageTag(parameters[2]);
				IEngineFactory uiFactory = MApi.lookup(IEngineFactory.class);
				IEngine engine = uiFactory.create(user, locale);
				ICase icase = engine.getCase(caze.getId().toString(), new String[]{"*"});
				ICaseDescription idesc = engine.getCaseDescription(icase);
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
					for (Entry<String, Object> entry : new TreeMap<String,Object>(((UiProcess)engine.getProcess(icase.getUri())).getProperties()).entrySet())
						System.out.println(entry.getKey() + "=" + entry.getValue());
			}
			
		} else
		if (cmd.equals("nodes")) {
			PCase caze = api.getEngine().getCase(UUID.fromString(parameters[0]));
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Id","CName","State","Type","Scheduled");
			for (PNodeInfo info : api.getEngine().storageGetFlowNodes(caze.getId(), null)) {
				PNode node = api.getEngine().getFlowNode(info.getId());
				if (all || node.getState() != STATE_NODE.CLOSED) {
					String scheduled = "-";
					Entry<String, Long> scheduledEntry = node.getNextScheduled();
					if (scheduledEntry != null) {
						long diff = scheduledEntry.getValue() - System.currentTimeMillis();
						if (diff > 0)
							scheduled = MTimeInterval.getIntervalAsString(diff);
					}
					table.addRowValues(node.getId(),node.getCanonicalName(),node.getState(),node.getType(), scheduled);
				}
			}
			table.print(System.out);
		} else
		if (cmd.equals("list")) {
			SearchCriterias criterias = new SearchCriterias(parameters);
			
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Id","CustomId","Uri","State","Close");
			for (PCaseInfo info : api.getEngine().storageSearchCases(criterias)) {
				PCase caze = api.getEngine().getCase(info.getId());
				if (all || caze.getState() != STATE_CASE.CLOSED)
					table.addRowValues(info.getId(), caze.getCustomId(), caze.getUri(), caze.getState(), caze.getClosedCode() + " " + caze.getClosedMessage() );
			}
			table.print(System.out);
		} else
		if (cmd.equals("resume")) {
			for (String id : parameters) {
				System.out.println("Resume: " + id);
				api.getEngine().resumeCase(UUID.fromString(id));
			}
		} else
		if (cmd.equals("suspend")) {
			for (String id : parameters) {
				System.out.println("Suspend: " + id);
				api.getEngine().suspendCase(UUID.fromString(id));
			}
		} else
		if (cmd.equals("archive")) {
			if (parameters == null) {
				System.out.println("Archive all");
				api.getEngine().archiveAll();
			} else {
				for (String id : parameters) {
					System.out.println("Archive: " + id);
					api.getEngine().archiveCase(UUID.fromString(id));
				}
			}
		} else
		if (cmd.equals("cancel")) {
			for (String id : parameters) {
				System.out.println("Cancel: " + id);
				api.getEngine().closeCase(UUID.fromString(id), true, -1, "cancelled by cmd");
			}
		} else {
			System.out.println("Unknown command");
		}
		
		return null;
	}

}
