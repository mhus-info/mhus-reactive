package de.mhus.cherry.reactive.engine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

import de.mhus.cherry.reactive.model.activity.EndPoint;
import de.mhus.cherry.reactive.model.activity.Gateway;
import de.mhus.cherry.reactive.model.activity.Point;
import de.mhus.cherry.reactive.model.activity.StartPoint;
import de.mhus.cherry.reactive.model.activity.Task;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.engine.EngineElement;
import de.mhus.cherry.reactive.model.engine.EnginePool;
import de.mhus.cherry.reactive.model.engine.EngineProcess;
import de.mhus.lib.core.MString;

public class ProcessDump {

	private EngineProcess process;
	private int width = 40;

	public ProcessDump(EngineProcess process) {
		this.process = process;
	}
	
	public void dump(PrintStream out) {
		for (String poolName : process.getPoolNames()) {
			HashSet<String> done = new HashSet<>();
			HashSet<String> needed = new HashSet<>();
			EnginePool pool = process.getPool(poolName);
			out.println(MString.rep('=', width ));
			out.println("Pool: " + pool.getCanonicalName());
			for (EngineElement startPoint : pool.getStartPoints()) {
				done.add(startPoint.getCanonicalName());
				needed.add(startPoint.getCanonicalName());
				while (needed.size() > 0) {
					String current = needed.iterator().next();
					needed.remove(current);
					EngineElement cur = pool.getElement(current);

					out.println();
					printElement(out,cur);
					out.println();
					printElementInfo(out, cur, done, needed);

					List<EngineElement> outputs = pool.getOutputElements(cur);
					while (true) {
						if (outputs.size() == 0) break;
						out.println("   |");
						EngineElement n = null;
						EngineElement f = null;
						for (EngineElement o : outputs) {
							out.print("  ");
							printElement(out,o);
							if (n == null && !done.contains(o.getCanonicalName())) 
								n = o;
							else
							if (!done.contains(o.getCanonicalName()))
								needed.add(o.getCanonicalName());
							if (f == null) f = o;
							done.add(o.getCanonicalName());
						}
						out.println();
						printElementInfo(out, f, done, needed);
						if (n == null) break;
						outputs = pool.getOutputElements(n);
					}
				}
			}
		}
	}

	private void printElement(PrintStream out, EngineElement cur) {
		if (cur.is(Point.class)) out.print('(');
		if (cur.is(Task.class)) out.print('[');
		if (cur.is(Gateway.class)) out.print('<');
		out.print(cur.getCanonicalName());
		if (cur.is(Point.class)) out.print(')');
		if (cur.is(Task.class)) out.print(']');
		if (cur.is(Gateway.class)) out.print('>');
	}
	
	private void printElementInfo(PrintStream out, EngineElement cur, HashSet<String> done, HashSet<String> needed) {
		out.println("   Lane: " + cur.getSwiminglane().getCanonicalName());
		for (Trigger trigger : cur.getTriggers()) {
			out.println("   Trigger: " + trigger.type() +": " + trigger.timer() + " " + trigger.name() + " -> " + trigger.activity().getCanonicalName() );
			if (!done.contains(trigger.activity().getCanonicalName()))
				needed.add(trigger.activity().getCanonicalName());
		}
	}
}
