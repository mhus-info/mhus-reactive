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
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.engine.util.EngineUtil;
import de.mhus.app.reactive.model.engine.EngineConst;
import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MProperties;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pcase-useraction",
        description = "Case user action")
@Service
public class CmdCaseUserAction extends AbstractCmd {

    @Argument(
            index = 0,
            name = "id",
            required = true,
            description = "case id or custom id",
            multiValued = false)
    String caseId;

    @Argument(
            index = 1,
            name = "action",
            required = true,
            description = "Action or '__list' or '__form'",
            multiValued = false)
    String action;
    
    @Argument(
            index = 2,
            name = "parameters",
            required = false,
            description = "Parameters",
            multiValued = true)
    String[] parameters;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        PCase caze = EngineUtil.getCase(api.getEngine(), caseId);
        MProperties values = IProperties.explodeToMProperties(parameters);

        if (action.equals("__list")) {
            action = EngineConst.ACTION_LIST;
        } else
        if (action.equals("__form")) {
            values.setString("action", parameters[0]);
            action = EngineConst.ACTION_FORM;
        }
        MProperties ret = api.getEngine().onUserCaseAction(caze.getId(), action, values);

        return ret;
    }
}
