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

import de.mhus.app.reactive.engine.util.JavaPackageProcessProvider;
import de.mhus.app.reactive.engine.util.ProcessTrace;
import de.mhus.app.reactive.model.engine.EElement;
import de.mhus.app.reactive.model.engine.EPool;
import de.mhus.app.reactive.model.engine.EProcess;
import de.mhus.app.reactive.model.engine.ProcessLoader;
import de.mhus.app.reactive.osgi.ReactiveAdmin;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "reactive", name = "pinspect-dump", description = "Dump deployed processes")
@Service
public class CmdInspectDump extends AbstractCmd {

    @Argument(index = 0, name = "Pool", required = false, description = "Pool", multiValued = false)
    String name;

    @Override
    public Object execute2() throws Exception {

        EProcess process = findProcess(name);
        ProcessTrace trace = new ProcessTrace(process);
        EPool pool = null;
        EElement element = null;

        MUri uri = MUri.toUri(name);
        if (uri.getScheme() != null) {
            if (uri.getPath() != null) {
                pool = process.getPool(uri.getPath());
                if (uri.getFragment() != null) {
                    element = pool.getElement(uri.getFragment());
                }
            }
        }

        if (element != null) trace.dump(System.out, pool, element);
        else if (pool != null) trace.dump(System.out, pool);
        else trace.dump(System.out);

        return null;
    }

    private EProcess findProcess(String string) throws MException {
        if (!string.startsWith("bpm://")) string = "bpm://" + string;
        ReactiveAdmin api = M.l(ReactiveAdmin.class);
        EProcess process = null;
        MUri uri = MUri.toUri(string);
        try {
            process = api.getEngine().getProcess(uri);
        } catch (Throwable t) {
            System.out.println("Deployed process not found: " + t);
        }
        if (process == null) {
            ProcessLoader loader = api.getProcessLoader(uri.getLocation());
            JavaPackageProcessProvider provider = new JavaPackageProcessProvider();
            provider.addProcess(loader, MString.beforeLastIndex(uri.getLocation(), '.'));
            process = provider.getProcess(uri.getLocation());
        }
        return process;
    }
}
