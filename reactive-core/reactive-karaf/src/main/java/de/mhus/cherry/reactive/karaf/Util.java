/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.karaf;

import java.util.HashMap;
import java.util.UUID;

import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.model.engine.EngineMessage;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.console.ConsoleTable;

public class Util {

    public static void printRuntime(ReactiveAdmin api, PCase caze, PNode pRuntime, String tblOpt)
            throws Exception {

        HashMap<UUID, String> cacheActivityNames = new HashMap<>();
        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Time", "Type", "From", "To", "Msg", "Node");
        table.getColumn(0).weight = 0;
        table.getColumn(1).weight = 0;
        table.getColumn(2).weight = 0;
        table.getColumn(3).weight = 0;
        table.getColumn(4).weight = 1;
        table.setAcceptHorizontalLine(true);
        EngineContext context = api.getEngine().createContext(caze);
        RuntimeNode aRuntime = api.getEngine().createRuntimeObject(context, pRuntime);
        boolean first = true;
        for (EngineMessage msg : aRuntime.getMessages()) {
            UUID from = msg.getFromNode();
            UUID to = msg.getToNode();
            String fromMsg = "";
            String toMsg = "";
            if (from != null) {
                String name = getActivityName(api, cacheActivityNames, from);
                fromMsg = name + '\n' + from.toString();
            }
            if (to != null) {
                String name = getActivityName(api, cacheActivityNames, to);
                toMsg = name + '\n' + to.toString();
            }

            if (!first) table.addRowValues(ConsoleTable.SEPARATOR_LINE);
            first = false;
            table.addRowValues(
                    MDate.toIso8601(msg.getTimestamp()),
                    msg.getType(),
                    fromMsg,
                    toMsg,
                    msg.getMessage().replace(',', '\n'),
                    msg.getServerIdent());
        }
        table.print(System.out);
    }

    private static String getActivityName(
            ReactiveAdmin api, HashMap<UUID, String> cacheActivityNames, UUID id) throws Exception {
        String name = null;
        if (cacheActivityNames != null) {
            name = cacheActivityNames.get(id);
            if (name != null) return name;
        }
        name =
                MString.afterLastIndex(
                        api.getEngine().storageGetFlowNodeInfo(id).getCanonicalName(), '.');
        if (cacheActivityNames != null) {
            cacheActivityNames.put(id, name);
        }
        return name;
    }
}
