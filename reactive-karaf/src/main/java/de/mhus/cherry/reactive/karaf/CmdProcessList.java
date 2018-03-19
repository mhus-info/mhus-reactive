package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.console.ConsoleTable;

@Command(scope = "reactive", name = "pls", description = "List processes")
@Service
public class CmdProcessList extends MLog implements Action {

	@Option(name="-a", aliases="--all", description="Print all versions (instead of active)",required=false)
	private boolean all;

	@Override
	public Object execute() throws Exception {
		
		ConsoleTable table = new ConsoleTable();
		table.fitToConsole();
		table.setHeaderValues("Registered", "Deployed", "Status","Info");
		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		for (String name : api.getAvailableProcesses()) {
			String deployName = api.getProcessDeployName(name);
			if (all || deployName != null) {
				String a = "undeployed";
				if (deployName != null) {
					boolean enabled = api.getEnginePersistence().isProcessEnabled(deployName);
					boolean active = api.getEnginePersistence().isProcessActive(deployName);
					a = (enabled ? "enabled" : "") + (active ? " active" : "");
				}
				String info = api.getProcessInfo(name);
				table.addRowValues(name, deployName, a, info);
			}
		}
		table.print(System.out);
		return null;
	}

}
