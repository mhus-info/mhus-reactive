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
package de.mhus.cherry.reactive.dev;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
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
			ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
			api.getEngine().start(uri);
			pos = (pos+1) % uris.length;
			Thread.sleep(interval * 1000);
			cnt++;
		}
		
	}

}
