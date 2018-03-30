/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
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
