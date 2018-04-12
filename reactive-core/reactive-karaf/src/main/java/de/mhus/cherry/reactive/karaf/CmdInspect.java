package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.engine.util.PoolValidator;
import de.mhus.cherry.reactive.engine.util.PoolValidator.Finding;
import de.mhus.cherry.reactive.engine.util.ProcessTrace;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.util.MUri;

@Command(scope = "reactive", name = "pinspect", description = "Inspect deployed processes")
@Service
public class CmdInspect extends MLog implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command:\n"
			+ " pools <process>\n"
			+ " validate <pool uri>\n"
			+ " dump <process>\n"
			+ "", multiValued=false)
    String cmd;

	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
	String[] parameters;
	
	@Option(name="-f", aliases="--full", description="Print full table output",required=false)
	private boolean full;

	@Override
	public Object execute() throws Exception {

		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		
		switch (cmd) {
		case "pools": {
			if (!parameters[0].startsWith("bpm://")) parameters[0] = "bpm://" + parameters[0];
			EProcess process = api.getEngine().getProcess(MUri.toUri(parameters[0]));
			for (String name : process.getPoolNames()) {
				EPool pool = process.getPool(name);
				System.out.println(parameters[0] + "/" + name);
				for (EElement start : pool.getStartPoints()) {
					System.out.println("--- Start: " + start.getCanonicalName());
				}
			}
		} break;
		case "validate": {
			MUri uri = MUri.toUri(parameters[0]);
			EProcess process = api.getEngine().getProcess(uri);
			EPool pool = api.getEngine().getPool(process, uri);
			PoolValidator validator = new PoolValidator(pool);
			validator.validate();
			for (Finding finding : validator.getFindings()) {
				System.out.println(finding);
			}
		} break;
		case "dump": {
			if (!parameters[0].startsWith("bpm://")) parameters[0] = "bpm://" + parameters[0];
			EProcess process = api.getEngine().getProcess(MUri.toUri(parameters[0]));
			ProcessTrace trace = new ProcessTrace(process);
			trace.dump(System.out);
		} break;
		default:
			System.out.println("Unknown command");
		}
		return null;
	}
}
