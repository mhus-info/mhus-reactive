/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.lib.core.util.MUri;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pls", description = "List processes")
@Service
public class CmdProcessList extends AbstractCmd {

    @Option(
            name = "-a",
            aliases = "--all",
            description = "Print all versions (instead of active)",
            required = false)
    private boolean all;

    @Option(name = "-p", aliases = "--pools", description = "Print also pools", required = false)
    private boolean pools;

    @Override
    public Object execute2() throws Exception {

        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Registered", "Deployed", "Status", "Info", "Deployed");
        ReactiveAdmin api = M.l(ReactiveAdmin.class);
        for (String name : api.getAvailableProcesses()) {
            String deployName = api.getProcessDeployName(name);
            if (all || deployName != null) {
                String a = "undeployed";
                String deployTime = "";
                if (deployName != null) {
                    boolean enabled = api.getEnginePersistence().isProcessEnabled(deployName);
                    boolean active = api.getEnginePersistence().isProcessActive(deployName);
                    a = (enabled ? "enabled" : "") + (active ? " active" : "");
                    deployTime = MDate.toIso8601(api.getProcessDeployTime(deployName));
                }
                String info = api.getProcessInfo(name);
                table.addRowValues(name, deployName, a, info, deployTime);
                if (pools && deployName != null) {
                    EProcess process =
                            api.getEngine().getProcess(MUri.toUri("reactive://" + deployName));
                    if (process != null) {
                        for (String poolName : process.getPoolNames()) {
                            //						EPool pool = process.getPool(poolName);
                            table.addRowValues("> Pool:", poolName, "", "");
                        }
                    }
                }
            }
        }
        table.print(System.out);
        return null;
    }
}
