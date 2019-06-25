/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.karaf;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pdeploy", description = "Deploy processes")
@Service
public class CmdProcessDeploy extends AbstractCmd {


	@Argument(index=0, name="name", required=true, description="Process name", multiValued=false)
    String name;

	@Option(name="-a", aliases="--activate", description="Activate",required=false)
	protected boolean activate;

	@Option(name="-n", aliases="--not", description="Do not add version",required=false)
	protected boolean notAdd;

	
	@Override
	public Object execute2() throws Exception {
		
		ReactiveAdmin api = M.l(ReactiveAdmin.class);
		api.deploy(name,!notAdd,activate);
		return null;
	}

}
