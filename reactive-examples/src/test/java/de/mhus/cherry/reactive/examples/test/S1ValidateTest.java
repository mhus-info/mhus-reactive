package de.mhus.cherry.reactive.examples.test;

import java.io.File;

import de.mhus.cherry.reactive.engine.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.PoolValidator;
import de.mhus.cherry.reactive.engine.ProcessDump;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.lib.errors.MException;
import junit.framework.TestCase;

public class S1ValidateTest extends TestCase {

	public void testValidate() throws MException {
		File f = new File("target/classes");
		System.out.println(f.getAbsolutePath());
		DefaultProcessLoader loader = new DefaultProcessLoader(new File[] {f});
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
			ProcessDump dump = new ProcessDump(process);
			dump.dump(System.out);
		}

	}
		
}
