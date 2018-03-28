package de.mhus.cherry.reactive.karaf;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.model.engine.SearchCriterias;
import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.ICaseDescription;
import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.IEngineFactory;
import de.mhus.cherry.reactive.model.ui.INode;
import de.mhus.cherry.reactive.model.ui.INodeDescription;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.console.ConsoleTable;

@Command(scope = "reactive", name = "pui", description = "UI lookup")
@Service
public class CmdUi extends MLog implements Action {

	@Argument(index=0, name="user", required=true, description="Username")
	String user;
	
	@Argument(index=1, name="locale", required=true, description="Locale")
	String locale;
	
	@Argument(index=2, name="cmd", required=true, description="Command:\n"
			+ " cases\n"
			+ " nodes\n"
			+ " case <id>\n"
			+ " node <id>\n"
			+ "", multiValued=false)
    String cmd;

	@Argument(index=3, name="parameters", required=false, description="Parameters", multiValued=true)
	String[] parameters;

	@Option(name="-a", aliases="--all", description="Print all",required=false)
	private boolean all;

	@Option(name="-f", aliases="--full", description="Print full table output",required=false)
	private boolean full;
	
	@Option(name="-p", aliases="--page", description="Page number",required=false)
	private int page = 0;
	
	@Option(name="-s", aliases="--size", description="Size per page",required=false)
	private int size = 100;
	
	@Override
	public Object execute() throws Exception {

		IEngine api = MApi.lookup(IEngineFactory.class).create(user, Locale.forLanguageTag(locale));
		
		if (cmd.equals("cases")) {
			SearchCriterias criterias = new SearchCriterias(parameters); 
			List<ICase> res = api.searchCases(criterias, page, size, null);
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Id","CustomId","Name","State","Uri");
			for (ICase info : res) {
				ICaseDescription desc = api.getCaseDescription(info);
				table.addRowValues(info.getId(),info.getCustomId(),desc.getDisplayName(),info.getState(),info.getUri());
			}
			table.print(System.out);
		} else
		if (cmd.equals("nodes")) {
			SearchCriterias criterias = new SearchCriterias(parameters); 
			List<INode> res = api.searchNodes(criterias, page, size, null);
			ConsoleTable table = new ConsoleTable(full);
			table.setHeaderValues("Id","CustomId","Name","State","Uri");
			for (INode info : res) {
				INodeDescription desc = api.getNodeDescription(info);
				table.addRowValues(info.getId(),info.getCustomId(),desc.getDisplayName(),info.getNodeState(),info.getUri());
			}
			table.print(System.out);
		} else
		if (cmd.equals("case")) {
			ICase info = api.getCase(parameters[0], parameters.length > 1 ? parameters : null);
			ICaseDescription desc = api.getCaseDescription(info);
			System.out.println("Id         : " + info.getId());
			System.out.println("Uri        : " + info.getUri());
			System.out.println("Name       : " + desc.getDisplayName());
			System.out.println("Description: " + desc.getDescription());
			for (Entry<String, String> entry : info.getProperties().entrySet()) {
				String name = desc.getPropertyName(entry.getKey());
				System.out.println(name + "=" + entry.getValue());
			}
		} else
		if (cmd.equals("node")) {
			INode info = api.getNode(parameters[0], parameters.length > 1 ? parameters : null);
			INodeDescription desc = api.getNodeDescription(info);
			System.out.println("Id         : " + info.getId());
			System.out.println("Uri        : " + info.getUri());
			System.out.println("Name       : " + desc.getDisplayName());
			System.out.println("Description: " + desc.getDescription());
			for (Entry<String, String> entry : info.getProperties().entrySet()) {
				String name = desc.getPropertyName(entry.getKey());
				System.out.println(name + "=" + entry.getValue());
			}
		} else {
			System.out.println("Unknown command");
		}
		
		return null;
	}

}