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
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.engine.util.EngineUtil;
import de.mhus.app.reactive.model.engine.PCase;
import de.mhus.app.reactive.model.ui.ICase;
import de.mhus.app.reactive.model.ui.ICaseDescription;
import de.mhus.app.reactive.model.ui.IEngine;
import de.mhus.app.reactive.model.ui.IEngineFactory;
import de.mhus.app.reactive.model.uimp.UiProcess;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MPeriod;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pcase-view", description = "Case modifications - view case details")
@Service
public class CmdCaseView extends AbstractCmd {

    @Argument(
            index = 0,
            name = "id",
            required = true,
            description = "case id or custom id",
            multiValued = false)
    String caseId;

    @Argument(
            index = 1,
            name = "user",
            required = false,
            description = "user",
            multiValued = false)
    String userId;

    @Argument(
            index = 2,
            name = "language",
            required = false,
            description = "Language",
            multiValued = false)
    String language;

    @Option(name = "-a", aliases = "--all", description = "Print all", required = false)
    private boolean all;

    @Override
    public Object execute2() throws Exception {

        ReactiveAdmin api = M.l(ReactiveAdmin.class);

        PCase caze = EngineUtil.getCase(api.getEngine(), caseId);
        System.out.println("Uri       : " + caze.getUri());
        System.out.println("CustomId  : " + caze.getCustomId());
        System.out.println("CustomerId: " + caze.getCustomerId());
        System.out.println("Name      : " + caze.getName());
        System.out.println("Id        : " + caze.getId());
        System.out.println("State     : " + caze.getState());
        System.out.println("Milestone : " + caze.getMilestone());
        System.out.println("CName     : " + caze.getCanonicalName());
        System.out.println("CreatedBy : " + caze.getCreatedBy());
        System.out.println("Created   : " + MDate.toIso8601(new Date(caze.getCreationDate())));
        System.out.println(
                "Scheduled : "
                        + (caze.getScheduled() > 0
                                ? MPeriod.getIntervalAsString(
                                        caze.getScheduled() - System.currentTimeMillis())
                                : "-"));
        System.out.println(
                "Close     : " + caze.getClosedCode() + " " + caze.getClosedMessage());
        System.out.println("Options   : " + caze.getOptions());
        String[] values = caze.getIndexValues();
        if (values != null) {
            for (int i = 0; i < values.length; i++)
                System.out.println("Value " + i + ": " + values[i]);
        } else {
            System.out.println("Values are empty");
        }
        if (caze.getParameters() != null) {
            System.out.println();
            for (Entry<String, Object> entry : caze.getParameters().entrySet()) {
                System.out.println("  " + entry.getKey() + "=" + entry.getValue());
            }
        }

        if (userId != null) {
            Locale locale = null;
            if (language != null) locale = Locale.forLanguageTag(language);
            IEngineFactory uiFactory = M.l(IEngineFactory.class);
            IEngine engine = uiFactory.create(userId, locale);
            ICase icase = engine.getCase(caze.getId().toString(), new String[] {"*"});
            ICaseDescription idesc = engine.getCaseDescription2(icase);
            System.out.println();
            System.out.println("User        : " + engine.getUser());
            System.out.println("Locale      : " + engine.getLocale());
            System.out.println("Display name: " + idesc.getDisplayName());
            System.out.println("Description : " + idesc.getDescription());
            for (Entry<String, String> entry : icase.getProperties().entrySet()) {
                String name = idesc.getPropertyName(entry.getKey());
                System.out.println(name + "=" + entry.getValue());
            }
            if (all)
                for (Entry<String, Object> entry :
                        new TreeMap<String, Object>(
                                        ((UiProcess) engine.getProcess(icase.getUri()))
                                                .getProperties())
                                .entrySet())
                    System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        return null;
    }
}
