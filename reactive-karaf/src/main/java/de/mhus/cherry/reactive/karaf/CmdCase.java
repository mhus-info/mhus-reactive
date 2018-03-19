package de.mhus.cherry.reactive.karaf;

import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.core.console.ConsoleTable;

@Command(scope = "reactive", name = "pcase", description = "Case modifiations")
@Service
public class CmdCase extends MLog implements Action {


	@Argument(index=0, name="cmd", required=true, description="Command: ", multiValued=false)
    String cmd;

	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;

	
	@Override
	public Object execute() throws Exception {

		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		
		if (cmd.equals("view")) {
			PCase caze = api.getEngine().getCase(UUID.fromString(parameters[0]));
			System.out.println("Uri      : " + caze.getUri());
			System.out.println("CustomId : " + caze.getCustomId());
			System.out.println("Name     : " + caze.getName());
			System.out.println("Id       : " + caze.getId());
			System.out.println("State    : " + caze.getState());
			System.out.println("CName    : " + caze.getCanonicalName());
			System.out.println("CreatedBy: " + caze.getCreatedBy());
			System.out.println("Created  : " + MDate.toIso8601(new Date(caze.getCreationDate())));
			System.out.println("Scheduled: " + (caze.getScheduled() > 0 ? MTimeInterval.getIntervalAsString(caze.getScheduled() - System.currentTimeMillis()) : "-"));
			System.out.println("Close    : " + caze.getClosedCode() + " " + caze.getClosedMessage());
			System.out.println("Options  : " + caze.getOptions());
		} else
		if (cmd.equals("nodes")) {
			PCase caze = api.getEngine().getCase(UUID.fromString(parameters[0]));
			ConsoleTable table = new ConsoleTable();
			table.fitToConsole();
			table.setHeaderValues("Id","CName","State","Type","Scheduled");
			for (PNodeInfo info : api.getEngine().storageGetFlowNodes(caze.getId(), null)) {
				PNode node = api.getEngine().getFlowNode(info.getId());
				String scheduled = "-";
				Entry<String, Long> scheduledEntry = node.getNextScheduled();
				if (scheduledEntry != null) {
					long diff = scheduledEntry.getValue() - System.currentTimeMillis();
					if (diff > 0)
						scheduled = MTimeInterval.getIntervalAsString(diff);
				}
				table.addRowValues(node.getId(),node.getCanonicalName(),node.getState(),node.getType(), scheduled);
			}
			table.print(System.out);
		} else
		if (cmd.equals("list")) {
			STATE_CASE state = null;
			if (parameters != null) state = STATE_CASE.valueOf(parameters[0].toUpperCase());
			ConsoleTable table = new ConsoleTable();
			table.fitToConsole();
			table.setHeaderValues("Id","CustomId","Uri","State","Close");
			for (PCaseInfo info : api.getEngine().storageGetCases(state)) {
				PCase caze = api.getEngine().getCase(info.getId());
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
		} else {
			System.out.println("Unknown command");
		}
		
		return null;
	}

}
