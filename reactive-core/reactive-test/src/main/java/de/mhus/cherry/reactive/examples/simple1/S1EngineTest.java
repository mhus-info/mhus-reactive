/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.examples.simple1;

import java.io.File;

import de.mhus.cherry.reactive.engine.util.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.util.PoolValidator;
import de.mhus.cherry.reactive.engine.util.ProcessTrace;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.lib.errors.MException;

public class S1EngineTest {

    public static void main(String[] args) throws MException {

        DefaultProcessLoader loader =
                new DefaultProcessLoader(new File[] {new File("target/classes")});
        DefaultProcessProvider provider = new DefaultProcessProvider();
        provider.addProcess(loader);

        for (String processName : provider.getProcessNames()) {
            System.out.println(">>> Process: " + processName);
            EProcess process = provider.getProcess(processName);
            for (String poolName : process.getPoolNames()) {
                System.out.println("   >>> Pool: " + poolName);
                EPool pool = process.getPool(poolName);
                PoolValidator validator = new PoolValidator(pool);
                validator.validate();
                for (PoolValidator.Finding finding : validator.getFindings()) {
                    System.out.println("   *** " + finding);
                }
            }
            ProcessTrace dump = new ProcessTrace(process);
            dump.dump(System.out);
        }
    }
}
