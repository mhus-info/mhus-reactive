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

@Mojo(name="designer",defaultPhase=LifecyclePhase.TEST)
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
			throw new MojoFailureException(e.getMessage(),e);
		}

	}

}
