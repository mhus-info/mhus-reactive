package de.mhus.cherry.reactive.engine;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

import de.mhus.cherry.reactive.model.activity.AGateway;
import de.mhus.cherry.reactive.model.activity.APoint;
import de.mhus.cherry.reactive.model.activity.ATask;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.console.ANSIConsole;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.Console.COLOR;
import de.mhus.lib.core.console.XTermConsole;

public class ProcessDump {

	private EProcess process;
	private int width = 40;
	private boolean ansi;

	public ProcessDump(EProcess process) {
		this.process = process;
		this.ansi = Console.get().isAnsi();
	}
		
	public void dump(PrintStream out) {
		for (String poolName : process.getPoolNames()) {
			HashSet<String> done = new HashSet<>();
			HashSet<String> needed = new HashSet<>();
			EPool pool = process.getPool(poolName);
			out.println(MString.rep('=', width ));
			out.println("Pool: " + pool.getCanonicalName());
			for (EElement startPoint : pool.getStartPoints()) {
				done.add(startPoint.getCanonicalName());
				needed.add(startPoint.getCanonicalName());
				while (needed.size() > 0) {
					String current = needed.iterator().next();
					needed.remove(current);
					EElement cur = pool.getElement(current);
					
					out.println("--------------------------------------------------------");
					printElement(out,cur);
					out.println("");
					printElementInfo(out, cur, done, needed);

					List<EElement> outputs = pool.getOutputElements(cur);
					while (true) {
						if (outputs.size() == 0) break;
						out.println("                       ||");
						out.println("                       \\/");
						EElement n = null;
						EElement f = null;
						for (EElement o : outputs) {
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

	private void printElement(PrintStream out, EElement cur) {
		if (ansi) out.print( ANSIConsole.ansiForeground(COLOR.RED));
		if (cur.is(APoint.class)) out.print('(');
		if (cur.is(ATask.class)) out.print('[');
		if (cur.is(AGateway.class)) out.print('<');
		out.print(cur.getCanonicalName());
		if (cur.is(APoint.class)) out.print(')');
		if (cur.is(ATask.class)) out.print(']');
		if (cur.is(AGateway.class)) out.print('>');
		if (ansi) out.print(ANSIConsole.ansiCleanup());
	}
	
	private void printElementInfo(PrintStream out, EElement cur, HashSet<String> done, HashSet<String> needed) {
		out.println("   Lane: " + cur.getSwimlane().getCanonicalName());
		for (Trigger trigger : cur.getTriggers()) {
			out.println("   Trigger: " + trigger.type() +": " + trigger.timer() + " " + trigger.name() + " -> " + trigger.activity().getCanonicalName() );
			if (!done.contains(trigger.activity().getCanonicalName()))
				needed.add(trigger.activity().getCanonicalName());
		}
	}
}
