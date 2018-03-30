/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;

@Command(scope = "reactive", name = "pdeploy", description = "Deploy processes")
@Service
public class CmdProcessDeploy extends MLog implements Action {


	@Argument(index=0, name="name", required=true, description="Process name", multiValued=false)
    String name;

	@Option(name="-a", aliases="--activate", description="Activate",required=false)
	protected boolean activate;

	@Option(name="-n", aliases="--not", description="Do not add version",required=false)
	protected boolean notAdd;

	
	@Override
	public Object execute() throws Exception {
		
		ReactiveAdmin api = MApi.lookup(ReactiveAdmin.class);
		api.deploy(name,!notAdd,activate);
		return null;
	}

}
