package de.mhus.cherry.reactive.examples.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.junit.Test;

import de.mhus.cherry.reactive.engine.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineConfiguration;
import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.engine.EngineListenerUtil;
import de.mhus.cherry.reactive.engine.PoolValidator;
import de.mhus.cherry.reactive.engine.ProcessDump;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.EngineMessage;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.cherry.reactive.util.engine.MemoryStorage;
import de.mhus.lib.errors.MException;
import junit.framework.TestCase;

public class Sample1Test extends TestCase {

	public void testEngine() throws Exception {
		
		File f = new File("target/classes");
		System.out.println(f.getAbsolutePath());
		DefaultProcessLoader loader = new DefaultProcessLoader(new File[] {f});
		DefaultProcessProvider provider = new DefaultProcessProvider();
		provider.addProcess(loader);

		EngineConfiguration config = new EngineConfiguration();
		config.storage = new MemoryStorage();
		config.archive = new MemoryStorage();
		config.aaa = new SimpleAaaProvider();
		config.parameters = new HashMap<>();
		config.parameters.put("process:de.mhus.cherry.reactive.examples.simple1.S1Process:versions", "0.0.1");
		config.listener = EngineListenerUtil.createStdErrListener();
		
		config.processProvider = provider;
		
		Engine engine = new Engine(config);

		{ // step second
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=second";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(100);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
//			assertEquals(4, i);
			if (i == 10) throw new MException();
		}

		archiveEngine(engine, config);
		
		{ // step third
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=third";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(100);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
//			assertEquals(4, i);
			if (i == 10) throw new MException();
		}
		
		archiveEngine(engine, config);
		
		{ // error1
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=error1";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(100);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
//			assertEquals(3, i);
			if (i == 10) throw new MException();
		}

		archiveEngine(engine, config);
		
		{ // fatal
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=fatal";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(100);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
//			assertEquals(3, i);
			if (i == 10) throw new MException();
		}

		archiveEngine(engine, config);
		
		{ // none - try stopped after retry
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=none";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(100);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				
				boolean found = false;
				for (PNodeInfo nodeId : engine.storageGetFlowNodes(caseId, STATE_NODE.RUNNING)) {
					PNode node = engine.getFlowNode(nodeId.getId());
					found = true;
					node.setScheduled(System.currentTimeMillis());
					config.storage.saveFlowNode(node);
				}
				if (!found) break;
			}
			
//			assertEquals(4, i);
			if (i == 10) throw new MException();
			
			engine.closeCase(caseId, true);
			
		}

		archiveEngine(engine, config);

		{ // custom startpoint
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool#de.mhus.cherry.reactive.examples.simple1.S1Start2";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(100);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
//			assertEquals(3, i);
			if (i == 10) throw new MException();
			
		}
		
		archiveEngine(engine, config);

	}
	
	private void archiveEngine(Engine engine, EngineConfiguration config) throws IOException, MException {
		
		// find runtime
		for (PNodeInfo nodeId : engine.storageGetFlowNodes(null, null)) {
			PNode node = engine.getFlowNode(nodeId.getId());
			if (node.getType() == TYPE_NODE.RUNTIME) {
				PCase caze = engine.getCase(node.getCaseId());
				EngineContext context = engine.createContext(caze);
				RuntimeNode runtime = engine.createRuntimeObject(context, node);
				System.out.println("Runtime protocol:");
				for (EngineMessage message : runtime.getMessages())
					System.out.println("*** " + message);
			}
		}
		
		engine.archiveAll();
		
		System.out.println(config.storage);

		{
			int cnt = 0;
			for (@SuppressWarnings("unused") PCaseInfo cazeId : config.storage.getCases(null)) cnt++;
			assertEquals(0, cnt);
		}
		{
			int cnt = 0;
			for (@SuppressWarnings("unused") PNodeInfo nodeId : config.storage.getFlowNodes(null,null)) cnt++;
			assertEquals(0, cnt);
		}
		
	}

	@Test
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
