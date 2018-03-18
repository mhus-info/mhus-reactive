package de.mhus.cherry.reactive.karaf;

import java.util.UUID;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;

@Command(scope = "reactive", name = "pengine", description = "Engine modifiations")
@Service
public class CmdProcessEngine extends MLog implements Action {


	@Argument(index=0, name="cmd", required=true, description="Command: parameter", multiValued=false)
    String cmd;

	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;

	
	@Override
	public Object execute() throws Exception {

		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		
		if (cmd.equals("parameter")) {
			PEngine persistent = api.getEnginePersistence();
			if (parameters != null) {
				MProperties properties = MProperties.explodeToMProperties(parameters);
				persistent.getParameters().putAll(properties);
			}
			System.out.println(persistent);
		} else
		if (cmd.equals("save")) {
			api.getEngine().saveEnginePersistence();
			System.out.println("OK");
		} else
		if (cmd.equals("load")) {
			api.getEngine().loadEnginePersistence();
			PEngine persistent = api.getEnginePersistence();
			System.out.println(persistent);
		} else
		if (cmd.equals("state")) {
			System.out.println(api.getEngineState());
		} else
		if (cmd.equals("suspend")) {
			api.setExecutionSuspended(true);
			System.out.println("OK");
		} else
		if (cmd.equals("resume")) {
			api.setExecutionSuspended(false);
			System.out.println("OK");
		} else
		if (cmd.equals("start")) {
			api.startEngine();
			System.out.println("OK");
		} else
		if (cmd.equals("stop")) {
			api.stopEngine();
			System.out.println("OK");
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
