package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.util.PoolValidator;
import de.mhus.cherry.reactive.engine.util.PoolValidator.Finding;
import de.mhus.cherry.reactive.engine.util.ProcessTrace;
import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.activity.ACondition;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AEndPoint;
import de.mhus.cherry.reactive.model.activity.AEvent;
import de.mhus.cherry.reactive.model.activity.AExclusiveGateway;
import de.mhus.cherry.reactive.model.activity.AGateway;
import de.mhus.cherry.reactive.model.activity.AParallelGateway;
import de.mhus.cherry.reactive.model.activity.APoint;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.activity.AServiceTask;
import de.mhus.cherry.reactive.model.activity.AStartPoint;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.activity.ATask;
import de.mhus.cherry.reactive.model.activity.AUserTask;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

@Command(scope = "reactive", name = "pinspect", description = "Inspect deployed processes")
@Service
public class CmdInspect extends MLog implements Action {

	@Argument(index=0, name="cmd", required=true, description="Command:\n"
			+ " pools <process>\n"
			+ " validate <pool uri>\n"
			+ " elements <pool uri>\n"
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
			EProcess process = findProcess(parameters[0]);
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
			EProcess process = findProcess(parameters[0]);
			EPool pool = getPool(process, uri);
			PoolValidator validator = new PoolValidator(pool);
			validator.validate();
			for (Finding finding : validator.getFindings()) {
				System.out.println(finding);
			}
		} break;
		case "dump": {
			EProcess process = findProcess(parameters[0]);
			ProcessTrace trace = new ProcessTrace(process);
			trace.dump(System.out);
		} break;
		case "elements": {
			MUri uri = MUri.toUri(parameters[0]);
			EProcess process = findProcess(parameters[0]);
			EPool pool = getPool(process, uri);
			
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Type","Name","Canonical Name","Swimlane","Outputs","Trigger");
			for (String name : pool.getElementNames()) {
				EElement element = pool.getElement(name);
				StringBuilder out = new StringBuilder();
				for (Output output : element.getOutputs()) {
					if (out.length() > 0) out.append("\n");
					out.append(output.name() + ":" + output.activity().getCanonicalName());
				}
				StringBuilder trig = new StringBuilder();
				for (Trigger trigger : element.getTriggers()) {
					if (trig.length() > 0) trig.append("\n");
					trig.append((trigger.abord() ? "" : "+") + trigger.type() + ":" + trigger.activity().getCanonicalName());
				}
				String type = "";
				if (element.is(APool.class)) type+="Pool\n";
				if (element.is(AEvent.class)) type+="Event\n";
				if (element.is(ASwimlane.class)) type+="Swimlane\n";
				if (element.is(AProcess.class)) type+="Process\n";
				if (element.is(ACondition.class)) type+="Condition\n";
				
				if (element.is(AUserTask.class)) type+="UserTask\n";
				else
				if (element.is(AServiceTask.class)) type+="ServiceTask\n";
				else
				if (element.is(ATask.class)) type+="Task\n";
				
				if (element.is(AExclusiveGateway.class)) type+="ExclusiveGateway\n";
				else
				if (element.is(AParallelGateway.class)) type+="ParallelGateway\n";
				else
				if (element.is(AGateway.class)) type+="Gateway\n";
				
				if (element.is(AStartPoint.class)) type+="StartPoint\n";
				else
				if (element.is(AEndPoint.class)) type+="EndPoint\n";
				else
				if (element.is(APoint.class)) type+="Point\n";
				
				type = type.trim();
				if (type.length() == 0) type = "Element";
				
				table.addRowValues(type,element.getName(),element.getCanonicalName(),element.getSwimlane() == null ? "none" : element.getSwimlane().getCanonicalName(),out,trig);
			}
			table.print(System.out);
		} break;
		default:
			System.out.println("Unknown command");
		}
		return null;
	}

	private EProcess findProcess(String string) throws MException {
		if (!string.startsWith("bpm://")) string = "bpm://" + string;
		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		EProcess process = null;
		MUri uri = MUri.toUri(string);
		try {
			process = api.getEngine().getProcess(uri);
		} catch (Throwable t) {
			System.out.println("Deployed process not found: " + t);
		}
		if (process == null) {
			ProcessLoader loader = api.getProcessLoader(uri.getLocation());
			DefaultProcessProvider provider = new DefaultProcessProvider();
			provider.addProcess(loader);
			process = provider.getProcess(uri.getLocation());
		}
		return process;
	}
	
	public EPool getPool(EProcess process, MUri uri) throws NotFoundException {
		String poolName = uri.getPath();
		if (MString.isEmpty(poolName))
			poolName = process.getProcessDescription().defaultPool();
		if (MString.isEmpty(poolName))
			throw new NotFoundException("default pool not found for process",uri);
		
		EPool pool = process.getPool(poolName);
		return pool;
	}
	
}
