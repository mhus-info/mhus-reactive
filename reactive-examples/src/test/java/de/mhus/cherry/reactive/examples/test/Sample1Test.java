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
import de.mhus.cherry.reactive.engine.mockup.EngineMockUp;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.EngineConst;
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
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.MException;
import junit.framework.TestCase;

public class Sample1Test extends TestCase {

	public void testEngine() throws Exception {
		
		long sleep = 10;
		
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
		config.parameters.put(EngineConst.ENGINE_EXECUTE_PARALLEL, "false");
		
		config.listener = EngineListenerUtil.createStdErrListener();
		
		config.processProvider = provider;
		
		Engine engine = new Engine(config);

		
		{ // step second
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_second.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=second";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);

			System.out.println(config.storage);
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			mockup.close();
		}

		archiveEngine(engine, config);
		
		{ // step third
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_third.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=third";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			mockup.close();
		}
		
		archiveEngine(engine, config);
		
		{ // error1
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_error1.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=error1";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			mockup.close();
		}

		archiveEngine(engine, config);
		
		{ // fatal
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_fatal.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=fatal";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			mockup.close();
		}

		archiveEngine(engine, config);
		
		{ // none - try stopped after retry
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_none.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=none";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();
				
				boolean found = false;
				for (PNodeInfo nodeId : engine.storageGetFlowNodes(caseId, STATE_NODE.RUNNING)) {
					PNode node = engine.getFlowNode(nodeId.getId());
					found = true;
					node.setScheduled(System.currentTimeMillis());
					config.storage.saveFlowNode(node);
				}
				if (!found) break;
			}
			
			mockup.close();
			engine.closeCase(caseId, true, -1, "test");
			
		}

		archiveEngine(engine, config);

		{ // custom startpoint
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_startpoint.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool#de.mhus.cherry.reactive.examples.simple1.S1Start2";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			mockup.close();
		}
		
		archiveEngine(engine, config);

		{ // fire external
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_fireexternal.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=external";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId = engine.start(uri);
			
			System.out.println(config.storage);
			mockup.step();
			
			boolean found = false;
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();

				if (i == 5) {
					for (PNodeInfo node : engine.storageGetFlowNodes(caseId, STATE_NODE.WAITING)) {
						if (node.getType() == TYPE_NODE.EXTERN) {
							engine.fireExternal(node.getId(), new MProperties());
							found = true;
						}
					}
				}
				
				PCase caze = engine.getCase(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(found);
			mockup.close();
		}
		
		archiveEngine(engine, config);
		
		{ // fire signal
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_firesignal.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=signal";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId1 = engine.start(uri);
			UUID caseId2 = engine.start(uri);
			
			System.out.println(config.storage);
			mockup.step();
			
			boolean found = false;
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();

				if (i == 5) {
					for (PNodeInfo node : engine.storageGetFlowNodes(caseId1, STATE_NODE.WAITING)) {
						if (node.getType() == TYPE_NODE.SIGNAL) {
							engine.fireSignal("signal", new MProperties());
							found = true;
						}
					}
				}
				
				PCase caze1 = engine.getCase(caseId1);
				PCase caze2 = engine.getCase(caseId2);
				if (caze1.getState() == STATE_CASE.CLOSED && caze2.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(found);
			mockup.close();
		}
		
		archiveEngine(engine, config);

		{ // fire message
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_firemessage.xml"));
			mockup.setWarn(false);
			String uri = "reactive://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=message";
			System.out.println("------------------------------------------------------------------------");
			System.out.println("URI: " + uri);
			UUID caseId1 = engine.start(uri);
			UUID caseId2 = engine.start(uri);
			
			System.out.println(config.storage);
			mockup.step();
			
			boolean found = false;
			int i = 0;
			for (i = 1; i <= 10; i++) {
				Thread.sleep(sleep);
				System.out.println();
				System.out.println("Step " + i);
				engine.step();
				System.out.println(config.storage);
				mockup.step();

				if (i == 5) {
					for (PNodeInfo node : engine.storageGetFlowNodes(caseId1, STATE_NODE.WAITING)) {
						if (node.getType() == TYPE_NODE.MESSAGE) {
							engine.fireMessage(caseId1, "message", new MProperties());
							found = true;
						}
					}
				}
				if (i == 6) {
					for (PNodeInfo node : engine.storageGetFlowNodes(caseId2, STATE_NODE.WAITING)) {
						if (node.getType() == TYPE_NODE.MESSAGE) {
							engine.fireMessage(caseId2, "message", new MProperties());
							found = true;
						}
					}
				}
				
				PCase caze1 = engine.getCase(caseId1);
				PCase caze2 = engine.getCase(caseId2);
				if (caze1.getState() == STATE_CASE.CLOSED && caze2.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(found);
			mockup.close();
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
	
//	private void assertCaseState(EngineConfiguration config, STATE_CASE ... states) throws IOException, MException {
//		System.out.print("CaseState: ");
//		for (PCaseInfo info : config.storage.getCases(null)) {
//			boolean found = false;
//			for (int i = 0; i < states.length; i++) {
//				if (states[i] != null && states[i] == info.getState()) {
//					states[i] = null;
//					found = true;
//					System.out.print(" " + info.getState());
//					break;
//				}
//			}
//			if (!found) throw new MException("case status not found",info.getState());
//		}
//		System.out.println();
//	}
//
//	private void assertNodeState(EngineConfiguration config, STATE_NODE ... states) throws IOException, MException {
//		System.out.print("NodeState: ");
//		for (PNodeInfo info : config.storage.getFlowNodes(null, null)) {
//			if (info.getType() == TYPE_NODE.RUNTIME) continue;
//			boolean found = false;
//			for (int i = 0; i < states.length; i++) {
//				if (states[i] != null && states[i] == info.getState()) {
//					states[i] = null;
//					found = true;
//					System.out.print(" " + info.getState());
//					break;
//				}
//			}
//			if (!found) throw new MException("node status not found",info.getState());
//		}
//		System.out.println();
//	}
//
//	private void assertNodeName(EngineConfiguration config, String ... names) throws IOException, MException {
//		System.out.print("NodeState: ");
//		for (PNodeInfo info : config.storage.getFlowNodes(null, null)) {
//			if (info.getType() == TYPE_NODE.RUNTIME) continue;
//			boolean found = false;
//			for (int i = 0; i < names.length; i++) {
//				if (names[i] != null && names[i].equals(info.getCanonicalName())) {
//					names[i] = null;
//					found = true;
//					System.out.print(" " + info.getCanonicalName());
//					break;
//				}
//			}
//			if (!found) throw new MException("node name not found",info.getCanonicalName());
//		}
//		System.out.println();
//	}
	
}
