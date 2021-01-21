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
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.app.reactive.engine.Engine;
import de.mhus.app.reactive.engine.util.Migrator;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.operation.DefaultMonitor;
import de.mhus.lib.core.operation.Monitor;
import de.mhus.lib.core.util.MUri;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "reactive",
        name = "pmigrate",
        description = "Manipulate process data in suspended cases")
@Service
public class CmdMigrate extends AbstractCmd {

    @Argument(
            index = 0,
            name = "uri",
            required = false,
            description =
                    "Filter for process, pool, activity in format bpm://<process>[:<version-range>][/<pool>[/<activity>]]",
            multiValued = false)
    String uriStr;

    @Option(
            name = "-t",
            aliases = "--test",
            description = "Test it and don't do it",
            required = false)
    private boolean test;

    @Option(
            name = "-i",
            aliases = "--ids",
            description = "Filter special case or node ids",
            required = false,
            multiValued = true)
    private String[] ids;

    @Option(
            name = "-s",
            aliases = "--suspend",
            description = "Suspend before migration",
            required = false)
    private boolean suspend;

    @Option(
            name = "-r",
            aliases = "--resume",
            description = "Resume after migration",
            required = false)
    private boolean resume;

    @Option(
            name = "-c",
            aliases = "--case",
            description =
                    "Case manipulation rule: name:<name> canonical:<name> milestone:<text> closeCode:<int> closeMessage<text> status<status> rm:<key> date:<key>=<date> string:<key>=<text> long: int: bool: uuid: double:",
            required = false,
            multiValued = true)
    private String[] caseRules;

    @Option(
            name = "-n",
            aliases = "--node",
            description =
                    "Node manipulating rule: name:<name> canonical:<name> rm:<key>  date:<key>=<date> string:<key>=<text> actor:<text> status<status> long: int: bool: uuid: double:",
            required = false,
            multiValued = true)
    private String[] nodeRules;

    @Option(name = "-v", aliases = "--verbose", description = "Verbose output", required = false)
    private boolean verbose;

    @Override
    public Object execute2() throws Exception {

        Monitor monitor = new DefaultMonitor(CmdMigrate.class);
        Migrator migrator = new Migrator(monitor);

        if (uriStr != null) {
            MUri uri = MUri.toUri(uriStr);
            migrator.setUri(uri);
        }

        migrator.setSelectedIds(ids);
        migrator.setTest(test);
        migrator.setCaseRules(caseRules);
        migrator.setNodeRules(nodeRules);
        migrator.setVerbose(verbose);

        ReactiveAdmin api = M.l(ReactiveAdmin.class);
        Engine engine = api.getEngine();
        migrator.setEngine(engine);

        if (suspend) migrator.suspend();

        migrator.migrate();

        if (resume) migrator.resume();

        return null;
    }
}
