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

import java.io.PrintStream;
import java.util.Date;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.engine.PCaseInfo;
import de.mhus.app.reactive.model.engine.SearchCriterias;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.pojo.MPojo;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pcase-deeplist",
        description = "Node modifications - list all cases")
@Service
public class CmdCaseDeepList extends AbstractCmd {

    @Argument(
            index = 0,
            name = "columns",
            required = false,
            description = "List of columns, separated by comma, case_id, case.*",
            multiValued = false)
    String cols;
    
    @Argument(
            index = 1,
            name = "search",
            required = false,
            description = "Search state=,name=,search=,index0..9=,uri=,case=",
            multiValued = true)
    String[] search;

    @Option(name = "-a", aliases = "--archive", description = "Use archive storage", required = false)
    private boolean archive;

    PojoModel PCASE_MODEL = MPojo.getAttributesModelFactory().createPojoModel(PCase.class);

    @Override
    public Object execute2() throws Exception {

        String[] colNames = cols.split(",");
        PrintStream out = System.out;
        
        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        SearchCriterias criterias = new SearchCriterias(search);

        for (PCaseInfo info : archive ? api.getEngine().archiveSearchCases(criterias) : api.getEngine().storageSearchCases(criterias)) {
                try {
                    PCase caze = api.getEngine().getCaseWithoutLock(info.getId());
                    
                    boolean first = true;
                    for (String col : colNames) {
                        if (!first)
                            out.print(",");
                        first = false;
                        printCol(caze, col, out);
                    }
                    out.println();
                } catch (Throwable t) {}
        }

        return null;
    }

    private void printCol(PCase caze, String col, PrintStream out) {
        int p = col.indexOf(':');
        String hint = null;
        if (p > 0) {
            hint = col.substring(p);
            col = col.substring(0,p-1);
        }
        if (col.startsWith("case.")) {
            printCol(caze.getParameters().get(col.substring(5)), hint, out);
        } else
        if (col.startsWith("case_")) {
            Object val = null;
            try {
                val = PCASE_MODEL.getAttribute(col.substring(5)).get(caze);
            } catch (Throwable t) {}
            printCol(val, hint, out);
        } else
            printCol("",null, out);
    }

    private void printCol(Object val, String hint, PrintStream out) {
        if (val == null) {
            return;
        }
        String str = "";
        if (MString.isEmpty(hint))
            str = val.toString();
        else
        if (hint.equals("date")) {
            Date d = MDate.toDate(val, null);
            if (d != null) {
                str = MDate.toIso8601(d);
            }
        }
        out.print("\"");
        str = str.replace("\"", "\"\"");
        out.print(str);
        out.print("\"");
    }
}
