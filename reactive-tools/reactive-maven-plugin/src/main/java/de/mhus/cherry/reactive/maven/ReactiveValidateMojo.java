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
package de.mhus.cherry.reactive.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.mhus.cherry.reactive.engine.util.JavaPackageProcessProvider;
import de.mhus.cherry.reactive.engine.util.PoolValidator;
import de.mhus.cherry.reactive.engine.util.ProcessTrace;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;

@Mojo(name = "validate", defaultPhase = LifecyclePhase.TEST)
public class ReactiveValidateMojo extends AbstractReactiveMojo {

    @Parameter(defaultValue = "false")
    private boolean dump;

    @Parameter(defaultValue = "true")
    private boolean failOnError;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            JavaPackageProcessProvider provider = createProvider();

            for (String processName : provider.getProcessNames()) {
                getLog().info(">>> Process: " + processName);
                EProcess process = provider.getProcess(processName);
                for (String poolName : process.getPoolNames()) {
                    getLog().info("  >>> Pool: " + poolName);
                    EPool pool = process.getPool(poolName);
                    PoolValidator validator = new PoolValidator(pool);
                    validator.validate();
                    for (PoolValidator.Finding finding : validator.getFindings()) {
                        switch (finding.getLevel()) {
                            case ERROR:
                                getLog().error("" + finding);
                                break;
                            case FATAL:
                                getLog().error("" + finding);
                                break;
                            case TRIVIAL:
                                getLog().info("" + finding);
                                break;
                            case WARN:
                                getLog().warn("" + finding);
                                break;
                        }
                        if (failOnError
                                && finding.getLevel().ordinal()
                                        > PoolValidator.LEVEL.WARN.ordinal()) {
                            throw new MojoExecutionException(finding.toString());
                        }
                    }
                }
                if (dump) {
                    ProcessTrace dump = new ProcessTrace(process);
                    dump.dump(System.out);
                }
            }

        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }
}
