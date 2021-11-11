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
import de.mhus.app.reactive.model.engine.PNode;
import de.mhus.app.reactive.model.engine.PNodeInfo;
import de.mhus.app.reactive.model.engine.SearchCriterias;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.pojo.MPojo;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pnode-query", description = "Query nodes")
@Service
public class CmdNodeQuery extends AbstractCmd {

    @Argument(
            index = 0,
            name = "columns",
            required = false,
            description =
                    "List of columns, separated by comma,\n"
                            + " node_* - node attribute\n"
                            + " case_* - case attribute\n"
                            + " option_* - option of the case, e.g. option_customerId\n"
                            + " node.* - node parameter\n"
                            + " case.* - case parameter",
            multiValued = false)
    String cols;

    @Argument(
            index = 1,
            name = "search",
            required = false,
            description = "Search state=,name=,search=,index0..9=,uri=,case=",
            multiValued = true)
    String[] search;

    @Option(
            name = "-a",
            aliases = "--archive",
            description = "Use archive storage",
            required = false)
    private boolean archive;

    PojoModel PNODE_MODEL = MPojo.getAttributesModelFactory().createPojoModel(PNode.class);
    PojoModel PCASE_MODEL = MPojo.getAttributesModelFactory().createPojoModel(PCase.class);

    @Override
    public Object execute2() throws Exception {

        String[] colNames = cols.split(",");
        PrintStream out = System.out;

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        SearchCriterias criterias = new SearchCriterias(search);

        for (PNodeInfo info :
                archive
                        ? api.getEngine().archiveSearchFlowNodes(criterias)
                        : api.getEngine().storageSearchFlowNodes(criterias)) {
            try {
                PNode node = api.getEngine().getNodeWithoutLock(info.getId());
                PCase caze = api.getEngine().getCaseWithoutLock(node.getCaseId());

                boolean first = true;
                for (String col : colNames) {
                    if (!first) out.print(",");
                    first = false;
                    printCol(node, caze, col, out);
                }
                out.println();
            } catch (Throwable t) {
            }
        }

        return null;
    }

    private void printCol(PNode node, PCase caze, String col, PrintStream out) {
        int p = col.indexOf(':');
        String hint = null;
        if (p > 0) {
            hint = col.substring(p);
            col = col.substring(0, p - 1);
        }
        if (col.startsWith("node.")) {
            printCol(node.getParameters().get(col.substring(5)), hint, out);
        } else if (col.startsWith("case.")) {
            printCol(caze.getParameters().get(col.substring(5)), hint, out);
        } else if (col.startsWith("option_")) {
            printCol(caze.getOptions().get(col.substring(7)), hint, out);
        } else if (col.startsWith("node_")) {
            Object val = null;
            try {
                val = PNODE_MODEL.getAttribute(col.substring(5)).get(node);
            } catch (Throwable t) {
            }
            printCol(val, hint, out);
        } else if (col.startsWith("case_")) {
            Object val = null;
            try {
                val = PCASE_MODEL.getAttribute(col.substring(5)).get(caze);
            } catch (Throwable t) {
            }
            printCol(val, hint, out);
        } else printCol("", null, out);
    }

    private void printCol(Object val, String hint, PrintStream out) {
        if (val == null) {
            return;
        }
        String str = "";
        if (MString.isEmpty(hint)) str = val.toString();
        else if (hint.equals("date")) {
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
