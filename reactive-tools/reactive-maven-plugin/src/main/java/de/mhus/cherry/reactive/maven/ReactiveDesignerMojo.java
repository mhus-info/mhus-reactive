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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.util.designer.DesignerUtil;
import de.mhus.cherry.reactive.util.designer.XmlModel;

@Mojo(name = "designer", defaultPhase = LifecyclePhase.TEST)
public class ReactiveDesignerMojo extends AbstractReactiveMojo {

    @Parameter(defaultValue = "target/designer")
    private String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {
            File dir = new File(target);
            if (!dir.exists()) dir.mkdirs();

            DefaultProcessProvider provider = createProvider();

            for (String processName : provider.getProcessNames()) {
                getLog().info(">>> Process: " + processName);
                EProcess process = provider.getProcess(processName);
                for (String poolName : process.getPoolNames()) {
                    getLog().info("  >>> Pool: " + poolName);
                    EPool pool = process.getPool(poolName);

                    File file = new File(dir, poolName + ".bpmn2");
                    getLog().info("  --- " + file);

                    XmlModel model = new XmlModel();
                    model.merge(process, pool);
                    DesignerUtil.saveInto(model, file);
                }
            }

        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }
}
