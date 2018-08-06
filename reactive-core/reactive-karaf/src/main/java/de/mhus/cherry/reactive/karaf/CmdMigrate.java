package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.util.Migrator;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.strategy.DefaultMonitor;
import de.mhus.lib.core.strategy.Monitor;
import de.mhus.lib.core.util.MUri;

@Command(scope = "reactive", name = "pmigrate", description = "Manipulate process data in suspended cases")
@Service
public class CmdMigrate extends MLog implements Action {

	@Argument(index=0, name="uri", required=true, description="Filter for process, pool, activity in format bpm://<process>[:<version-range>][/<pool>[/<activity>]]", multiValued=false)
    String uriStr;

	@Option(name="-t", aliases="--test", description="Test it and don't do it",required=false)
	private boolean test;

	@Option(name="-i", aliases="--ids", description="Filter special case or node ids",required=false, multiValued=true)
	private String[] ids;
	
	@Option(name="-s", aliases="--suspend", description="Suspend before migration",required=false)
	private boolean suspend;

	@Option(name="-r", aliases="--resume", description="Resume after migration",required=false)
	private boolean resume;

	@Option(name="-c", aliases="--case", description="Case manipulation rule: name:<name> canonical:<name> milestone:<text> closeCode:<int> closeMessage<text> status<status> rm:<key> date:<key>=<date> string:<key>=<text> long: int: bool: uuid: double:",required=false, multiValued=true)
	private String[] caseRules;

	@Option(name="-n", aliases="--node", description="Node manipulating rule: name:<name> canonical:<name> rm:<key>  date:<key>=<date> string:<key>=<text> actor:<text> status<status> long: int: bool: uuid: double:",required=false, multiValued=true)
	private String[] nodeRules;

	@Override
	public Object execute() throws Exception {

		Monitor monitor = new DefaultMonitor(CmdMigrate.class);
		Migrator migrator = new Migrator(monitor);
		
		if (uriStr != null) {
			MUri uri = MUri.toUri(uriStr);
			migrator.setUri(uri);
		}
		
		migrator.setSelectedIds(ids);
		migrator.setTest(test);
		migrator.setCaseRules(caseRules);
		migrator.setNodeRules(nodeRules);
		
		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		Engine engine = api.getEngine();
		migrator.setEngine(engine);
		
		if (suspend)
			migrator.suspend();
		
		migrator.migrate();
		
		if (resume)
			migrator.resume();
		
		return null;
	}

}
