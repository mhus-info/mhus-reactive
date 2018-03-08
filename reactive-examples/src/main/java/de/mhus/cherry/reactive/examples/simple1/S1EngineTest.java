package de.mhus.cherry.reactive.examples.simple1;

import java.io.File;

import de.mhus.cherry.reactive.engine.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.PoolValidator;
import de.mhus.cherry.reactive.engine.ProcessDump;
import de.mhus.cherry.reactive.model.engine.EnginePool;
import de.mhus.cherry.reactive.model.engine.EngineProcess;
import de.mhus.lib.errors.MException;

public class S1EngineTest {

	public static void main(String[] args) throws MException {
		
		DefaultProcessLoader loader = new DefaultProcessLoader(new File[] {new File("target/classes") });
		DefaultProcessProvider provider = new DefaultProcessProvider();
		provider.addProcess(loader);
		
		
		
		
		for (String processName : provider.getProcessNames()) {
			System.out.println(">>> Process: " + processName);
			EngineProcess process = provider.getProcess(processName);
			for (String poolName : process.getPoolNames()) {
				System.out.println("   >>> Pool: " + poolName);
				EnginePool pool = process.getPool(poolName);
				PoolValidator validator = new PoolValidator(pool);
				validator.validate();
				for (PoolValidator.Finding finding : validator.getFindings()) {
					System.out.println("   *** " + finding);
				}
			}
			ProcessDump dump = new ProcessDump(process);
			dump.dump(System.out);
		}
		
		
	}

}
