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

import java.util.Date;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.app.reactive.model.engine.PCaseInfo;
import de.mhus.app.reactive.model.engine.SearchCriterias;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pcase-list",
        description = "Case modifications - list all cases")
@Service
public class CmdCaseList extends AbstractCmd {

    @Argument(
            index = 0,
            name = "search",
            required = false,
            description = "Search state=,name=,search=,index0..9=,uri=",
            multiValued = true)
    String[] search;

    @Option(name = "-1", aliases = "--one", description = "Print in one table", required = false)
    private boolean one;

    @Option(name = "-a", aliases = "--all", description = "Print all", required = false)
    private boolean all;

    @Option(name = "-c", aliases = "--count", description = "Print count only", required = false)
    private boolean count;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        SearchCriterias criterias = new SearchCriterias(search);

        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Id", "CustomId", "Customer", "Modified", "Uri", "State", "Close");
        table.getColumn(0).minWidth = 32;
        int c = 0;
        for (PCaseInfo info : api.getEngine().storageSearchCases(criterias)) {
            if (all || info.getState() != STATE_CASE.CLOSED) {
                try {
                    PCase caze = api.getEngine().getCaseWithoutLock(info.getId());
                    if (count) {
                        c++;
                    } else {
                        table.addRowValues(
                                info.getId(),
                                caze.getCustomId(),
                                caze.getCustomerId(),
                                new Date(info.getModified()),
                                caze.getUri(),
                                caze.getState(),
                                caze.getClosedCode() + " " + caze.getClosedMessage());
                    }
                } catch (Throwable t) {
                    table.addRowValues(
                            info.getId(),
                            info.getCustomId(),
                            info.getCustomerId(),
                            new Date(info.getModified()),
                            info.getUri(),
                            info.getState(),
                            t.getMessage());
                }
            }
            if (!count && !one && table.size() >= 100) {
                table.print(System.out);
                table.clear();
                System.out.println();
            }
        }
        if (count) System.out.println(c);
        else if (table.size() > 0) table.print(System.out);

        return null;
    }
}
