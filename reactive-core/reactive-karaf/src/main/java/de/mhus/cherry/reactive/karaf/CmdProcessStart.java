package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;

@Command(scope = "reactive", name = "pstart", description = "Start case")
@Service
public class CmdProcessStart extends MLog implements Action {


	@Argument(index=0, name="uri", required=true, description="Process uri", multiValued=false)
    String uri;

	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;

	
	@Override
	public Object execute() throws Exception {

		if (uri.startsWith(EngineConst.SCHEME_REACTIVE + ":") ) {
			MProperties properties = MProperties.explodeToMProperties(parameters);
			
			ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
			api.getEngine().start(uri, properties);
		} else {
			System.out.println("Unknown schema: " + uri);
		}
		return null;
	}

}
