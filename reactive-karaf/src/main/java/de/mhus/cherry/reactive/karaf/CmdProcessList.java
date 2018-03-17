package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.lib.core.MLog;

@Command(scope = "reactive", name = "pls", description = "List processes")
@Service
public class CmdProcessList extends MLog implements Action {

	@Option(name="-a", aliases="--all", description="Print all versions (instead of active)",required=false)
	private boolean all;

	@Override
	public Object execute() throws Exception {
		return null;
	}

}
