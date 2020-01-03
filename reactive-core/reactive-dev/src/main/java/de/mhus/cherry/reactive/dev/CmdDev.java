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

import de.mhus.cherry.reactive.engine.util.EngineUtil;
import de.mhus.cherry.reactive.engine.util.PCaseLock;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pdev", description = "reactive development tool")
@Service
public class CmdDev extends AbstractCmd {

	@Argument(index=0, name="cmd", required=true, description="Command:\n"
			+ " cancelall - cancel all cases\n"
			+ "", multiValued=false)
    String cmd;

	@Argument(index=1, name="parameters", required=false, description="Parameters", multiValued=true)
	String[] parameters;
	
	@Option(name="-a", aliases="--all", description="Print all",required=false)
	private boolean all;

	@Override
	public Object execute2() throws Exception {

		ReactiveAdmin api = M.l(ReactiveAdmin.class);
		
		if (cmd.equals("cancelall")) {

            SearchCriterias criterias = new SearchCriterias(parameters);
            for (PCaseInfo info : api.getEngine().storageSearchCases(criterias)) {
                if (info.getState() != STATE_CASE.CLOSED) {
                    try (PCaseLock lock = EngineUtil.getCaseLock(api.getEngine(), info.getId().toString())) {
                        PCase caze = lock.getCase();
                        System.out.println("Cancel: " + caze);
                        lock.closeCase(true, -1, "cancelled by cmd");
                    } catch (Throwable t) {
                        System.out.println("Error in " + info);
                        t.printStackTrace();
                    }
                }
            }
		} else {
			System.out.println("Unknown command");
		}
		
		return null;
	}

}
