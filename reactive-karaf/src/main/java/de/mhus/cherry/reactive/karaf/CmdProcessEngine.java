package de.mhus.cherry.reactive.karaf;

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


	@Argument(index=0, name="cmd", required=true, description="Command", multiValued=false)
    String cmd;

	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;

	
	@Override
	public Object execute() throws Exception {

		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		
		if (cmd.equals("parameter")) {
			MProperties properties = MProperties.explodeToMProperties(parameters);
			PEngine persistent = api.getEngine().getEnginePersistence();
			persistent.getParameters().putAll(properties);
			System.out.println(persistent);
		} else
		if (cmd.equals("save")) {
			api.getEngine().saveEnginePersistence();
			System.out.println("OK");
		} else
		if (cmd.equals("load")) {
			api.getEngine().loadEnginePersistence();
			PEngine persistent = api.getEngine().getEnginePersistence();
			System.out.println(persistent);
			
		}
		
		return null;
	}

}
