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
package de.mhus.cherry.reactive.dev;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.Console.COLOR;

@Command(scope = "reactive", name = "pstress", description = "Execute cases all the time")
@Service
public class CmdStress extends MLog implements Action {

	@Argument(index=0, name="uris", required=false, description="URIs to execute", multiValued=true)
	String[] uris;

	@Option(name="-i", aliases="--interval", description="Interval in seconds (default 5)",required=false)
	private int interval = 5;

	@Option(name="-c", aliases="--cnt", description="Start counter for $cnt$",required=false)
	private int cnt = 10000;
	
	@Override
	public Object execute() throws Exception {
		
		Console console = Console.get();
		
		int pos = 0;
		while (true) {
			String uri = uris[pos];
			uri = uri.replace("$cnt$", ""+cnt);
			console.setColor(COLOR.RED, null);
			System.out.println(">>> " + cnt + ": " + uri);
			console.cleanup();
			ReactiveAdmin api = M.l(ReactiveAdmin.class);
			api.getEngine().start(uri);
			pos = (pos+1) % uris.length;
			Thread.sleep(interval * 1000);
			cnt++;
		}
		
	}

}
