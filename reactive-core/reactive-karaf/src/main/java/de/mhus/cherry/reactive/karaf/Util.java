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

	public static void printRuntime(ReactiveAdmin api, PCase caze, PNode pRuntime, boolean full) throws Exception {
		
		HashMap<UUID, String> cacheActivityNames = new HashMap<>();
		ConsoleTable table = new ConsoleTable(full);
		table.setHeaderValues("Time","Type","From","To","Msg");
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
			table.addRowValues(MDate.toIso8601(msg.getTimestamp()), msg.getType(), fromMsg, toMsg, msg.getMessage().replace(',', '\n') ); 
		}
		table.print(System.out);
	}

	private static String getActivityName(ReactiveAdmin api, HashMap<UUID, String> cacheActivityNames, UUID id) throws Exception {
		String name = null;
		if (cacheActivityNames != null) {
			name = cacheActivityNames.get(id);
			if (name != null) return name;
		}
		name = MString.afterLastIndex(api.getEngine().storageGetFlowNodeInfo(id).getCanonicalName(), '.');
		if (cacheActivityNames != null) {
			cacheActivityNames.put(id, name);
		}
		return name;
	}

}
