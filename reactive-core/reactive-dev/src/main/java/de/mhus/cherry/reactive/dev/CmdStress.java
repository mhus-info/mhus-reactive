/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
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
package de.mhus.cherry.reactive.dev;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.Result;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.Console.COLOR;
import de.mhus.lib.core.logging.ITracer;
import de.mhus.osgi.api.karaf.AbstractCmd;
import io.opentracing.Scope;

@Command(scope = "reactive", name = "pstress", description = "Execute cases all the time")
@Service
public class CmdStress extends AbstractCmd {

    @Argument(
            index = 0,
            name = "uris",
            required = false,
            description = "URIs to execute",
            multiValued = true)
    String[] uris;

    @Option(
            name = "-i",
            aliases = "--interval",
            description = "Interval in seconds (default 5)",
            required = false)
    private int interval = 5;

    @Option(
            name = "-c",
            aliases = "--cnt",
            description = "Start counter for $cnt$",
            required = false)
    private int cnt = 10000;

    @Option(name = "-m", aliases = "--max", description = "Max active processes", required = false)
    private int max = 0;

    @Reference private Session session;

    private static volatile boolean stopped = true;
    private static volatile boolean running = false;

    @Override
    public Object execute2() throws Exception {

        if (uris != null && uris.length == 1 && uris[0].equals("status")) {
            System.out.println("Stopped: " + stopped);
            System.out.println("Running: " + running);
            return null;
        }
        if (uris == null || uris.length == 1 && uris[0].equals("stop")) {
            stopped = true;
            System.out.println(">>> Stopping ...");
            return null;
        }

        Console console = Console.get();

        stopped = false;
        int pos = 0;
        while (!stopped) {
            running = false;
            String uri = uris[pos];
            uri = uri.replace("$cnt$", "" + cnt);
            console.setColor(COLOR.RED, null);
            System.out.println(">>> " + cnt + ": " + uri);
            console.cleanup();
            ReactiveAdmin api = M.l(ReactiveAdmin.class);
            try (Scope scope = ITracer.get().start("stress:" + uris[pos], "stress", "cnt", cnt)) {
                api.getEngine().start(uri);
            }
            pos = (pos + 1) % uris.length;
            cnt++;

            if (max > 0) {
                while (true) {
                    Result<PCaseInfo> cases = api.getEngine().storageGetCases(null);
                    int cs = 0;
                    for (PCaseInfo caze : cases) if (caze.getState() != STATE_CASE.CLOSED) cs++;
                    if (cs < max || stopped) break;
                    System.out.println("=== Too much cases " + cs);
                    if (session.getKeyboard().available() > 0) return null;
                    Thread.sleep(1000);
                }
            }
            if (session.getKeyboard().available() > 0) return null;
            if (interval > 0) Thread.sleep(interval * 1000);
        }
        System.out.println("### Stopped");
        running = true;
        return null;
    }
}
