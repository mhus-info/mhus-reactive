/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.mhus.cherry.reactive.examples.test;

import java.io.File;

import de.mhus.cherry.reactive.engine.util.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.util.PoolValidator;
import de.mhus.cherry.reactive.engine.util.ProcessTrace;
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
			ProcessTrace dump = new ProcessTrace(process);
			dump.dump(System.out);
		}

	}
		
}
