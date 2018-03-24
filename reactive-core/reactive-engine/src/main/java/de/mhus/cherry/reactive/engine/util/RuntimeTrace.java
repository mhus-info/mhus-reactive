package de.mhus.cherry.reactive.engine.util;

import java.io.PrintStream;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.EngineMessage;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.lib.core.console.ANSIConsole;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.Console.COLOR;

public class RuntimeTrace {

	private RuntimeNode runtime;
	private boolean ansi;
	private int width = 40;

	public RuntimeTrace(RuntimeNode runtime) {
		this.runtime = runtime;
		this.ansi = Console.get().isAnsi();
		trace();
	}

	private void trace() {
		//TODO create model
	}
	
	public void dumpMessages(PrintStream out) {
		for (EngineMessage msg : runtime.getMessages()) {
			switch (msg.getType()) {
			case CONNECT:
				if (ansi) out.print(ANSIConsole.ansiForeground(COLOR.BLUE));
				out.print("+++ ");
				break;
			case ERROR:
				if (ansi) out.print(ANSIConsole.ansiForeground(COLOR.RED));
				out.print("*** ");
				break;
			case START:
				if (ansi) out.print(ANSIConsole.ansiForeground(COLOR.BLUE));
				out.print("+++ ");
				break;
			case FLOW:
				out.print("--- ");
				break;
			case OTHER:
			default:
				out.print("    ");
				break;
			
			}
			out.println(msg);
			if (ansi) out.print(ANSIConsole.ansiCleanup());
		}
	}

	public void dumpTrace(PrintStream out, UUID nodeId) {
		for (EngineMessage msg : runtime.getMessages()) {
			if (nodeId.equals(msg.getFromNode()) || nodeId.equals(msg.getToNode()))
				out.println(msg);
		}
	}
	
	public boolean isAnsi() {
		return ansi;
	}

	public void setAnsi(boolean ansi) {
		this.ansi = ansi;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	
}
