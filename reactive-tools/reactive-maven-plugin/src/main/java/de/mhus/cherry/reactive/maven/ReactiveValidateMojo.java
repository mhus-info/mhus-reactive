package de.mhus.cherry.reactive.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.util.PoolValidator;
import de.mhus.cherry.reactive.engine.util.ProcessTrace;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;

@Mojo(name="validate",defaultPhase=LifecyclePhase.TEST)
public class ReactiveValidateMojo extends AbstractReactiveMojo {

	@Parameter(defaultValue = "false")
	private boolean dump;

	@Parameter(defaultValue = "true")
	private boolean failOnError;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		try {
			
			DefaultProcessProvider provider = createProvider();

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
							getLog().error(""+finding);
							break;
						case FATAL:
							getLog().error(""+finding);
							break;
						case TRIVIAL:
							getLog().info(""+finding);
							break;
						case WARN:
							getLog().warn(""+finding);
							break;
						}
						if (failOnError && finding.getLevel().ordinal() > PoolValidator.LEVEL.WARN.ordinal()) {
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
			throw new MojoFailureException(e.getMessage(),e);
		}
	}

}
