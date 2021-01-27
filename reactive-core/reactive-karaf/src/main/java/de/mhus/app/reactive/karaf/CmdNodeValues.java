/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
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
package de.mhus.app.reactive.karaf;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.app.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.app.reactive.model.engine.PNodeInfo;
import de.mhus.app.reactive.model.engine.SearchCriterias;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pnode-values", description = "Node modifications - search nodes and print values")
@Service
public class CmdNodeValues extends AbstractCmd {

    @Argument(
            index = 0,
            name = "search",
            required = false,
            description = "Search",
            multiValued = true)
    String[] search;
    
    @Option(name = "-a", aliases = "--all", description = "Print all", required = false)
    private boolean all;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);
        
        SearchCriterias criterias = new SearchCriterias(search);

        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Id", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        for (PNodeInfo info : api.getEngine().storageSearchFlowNodes(criterias)) {
            if (all
                    || (info.getState() != STATE_NODE.CLOSED
                            && info.getType() != TYPE_NODE.RUNTIME)) {
                table.addRowValues(
                        info.getId(),
                        info.getIndexValue(0),
                        info.getIndexValue(1),
                        info.getIndexValue(2),
                        info.getIndexValue(3),
                        info.getIndexValue(4),
                        info.getIndexValue(5),
                        info.getIndexValue(6),
                        info.getIndexValue(7),
                        info.getIndexValue(8),
                        info.getIndexValue(9));
            }
        }
        table.print(System.out);

        return null;
    }
}
