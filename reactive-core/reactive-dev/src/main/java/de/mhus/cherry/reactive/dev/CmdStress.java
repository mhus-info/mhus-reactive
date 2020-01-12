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

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.Console.COLOR;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pstress", description = "Execute cases all the time")
@Service
public class CmdStress extends AbstractCmd {

	@Argument(index=0, name="uris", required=false, description="URIs to execute", multiValued=true)
	String[] uris;

	@Option(name="-i", aliases="--interval", description="Interval in seconds (default 5)",required=false)
	private int interval = 5;

	@Option(name="-c", aliases="--cnt", description="Start counter for $cnt$",required=false)
	private int cnt = 10000;
	
    @Option(name="-m", aliases="--max", description="Max active processes",required=false)
    private int max = 0;
    
    @Option(name="-s", aliases="--size", description="How much to create for each interval",required=false)
    private int size = 1;
    
	@Override
	public Object execute2() throws Exception {
		
		Console console = Console.get();
		
		int pos = 0;
		while (true) {
		    for (int i = 0; i < size; i++) {
    			String uri = uris[pos];
    			uri = uri.replace("$cnt$", ""+cnt);
    			console.setColor(COLOR.RED, null);
    			System.out.println(">>> " + cnt + ": " + uri);
    			console.cleanup();
    			ReactiveAdmin api = M.l(ReactiveAdmin.class);
    			api.getEngine().start(uri);
    			pos = (pos+1) % uris.length;
    			cnt++;
    			
    			if (max > 0) {
    			    while (true) {
        			    Result<PCaseInfo> cases = api.getEngine().storageGetCases(null);
        			    int cs = 0;
        			    for (PCaseInfo caze : cases)
        			        if (caze.getState() != STATE_CASE.CLOSED)
        			            cs++;
        			    if (cs < max) break;
        			    System.out.println("=== To much cases " + cs);
        	            Thread.sleep(interval * 1000);
    			    }
    			}
		    }
			Thread.sleep(interval * 1000);
		}
		
	}

}
