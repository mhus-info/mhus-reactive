/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.examples.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineConfiguration;
import de.mhus.cherry.reactive.engine.EngineContext;
import de.mhus.cherry.reactive.engine.mockup.EngineMockUp;
import de.mhus.cherry.reactive.engine.util.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.util.EngineListenerUtil;
import de.mhus.cherry.reactive.engine.util.PCaseLock;
import de.mhus.cherry.reactive.engine.util.RuntimeTrace;
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
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.Console.COLOR;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public class S1ActivitiesTest  {

	private EngineConfiguration config;
	private Engine engine;
	long sleep = 10;
	private Console console;

	@Test
	public void testSubStart() throws Exception {
		
		createEnigne();

		try { // exclusive kirk
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_substart.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=substart&text2=kirk";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId1 = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze1 = engine.getCaseWithoutLock(caseId1);
				if (caze1.getState() == STATE_CASE.CLOSED) {
					assertEquals("spock", caze1.getParameters().get("text2"));
					break;
				}
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
		
		archiveEngine(engine, config);

	}
	
    @Test
	public void testParallel2() throws Exception {
		
		createEnigne();
		
		try { // step second
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_parallel2.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=parallel2";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);

			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze = engine.getCaseWithoutLock(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}

    @Test
	public void testParallel1() throws Exception {
		
		createEnigne();
		
		try { // step second
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_parallel1.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=parallel1";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);

			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze = engine.getCaseWithoutLock(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}

    @Test
	public void testTriggerTimer() throws Exception {
		
		createEnigne();
		
		try {
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_triggertimer.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=triggertimer";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);

			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze1 = engine.getCaseWithoutLock(caseId);
				if (caze1.getState() == STATE_CASE.CLOSED) {
					assertEquals(2, caze1.getClosedCode());
					break;
				}
				
				if (i == 4) {
				    ((MemoryStorage)config.storage).dumpNodes();
					MThread.sleep(1200);
				}
				
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}

    @Test
	public void testTriggerMessage() throws Exception {
		
		createEnigne();
		
		try {
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_triggermessage.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=trigger";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);

			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze1 = engine.getCaseWithoutLock(caseId);
				if (caze1.getState() == STATE_CASE.CLOSED) {
					assertEquals(2, caze1.getClosedCode());
					break;
				}
				
				if (i == 4) {
				    ((MemoryStorage)config.storage).dumpNodes();
					engine.fireSignal("signal", new MProperties());
				}
				
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}

    @Test
	public void testTriggerSignal() throws Exception {
		
		createEnigne();
		
		try {
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_triggersignal.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=trigger";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);

			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze1 = engine.getCaseWithoutLock(caseId);
				if (caze1.getState() == STATE_CASE.CLOSED) {
					assertEquals(2, caze1.getClosedCode());
					break;
				}
				
				if (i == 4) {
				    // ((MemoryStorage)config.storage).dumpNodes();
					engine.fireMessage(caseId, "message", new MProperties());
				}
				
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}

    @Test
	public void testSecond() throws Exception {
				
		createEnigne();
		
		try { // step second
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_second.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=second";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);

			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze = engine.getCaseWithoutLock(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}
	
    @Test
	public void testThird() throws Exception {
		
		createEnigne();
			
		try { // step third
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_third.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=third";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze = engine.getCaseWithoutLock(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
		
		archiveEngine(engine, config);
	}
	
    @Test
	public void testError1() throws Exception {
		
		createEnigne();
		
		try { // error1
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_error1.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=error1";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze = engine.getCaseWithoutLock(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}
	
    @Test
	public void testFatal() throws Exception {
		
		createEnigne();
		
		try { // fatal
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_fatal.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=fatal";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze = engine.getCaseWithoutLock(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}
	
    @Test
	public void testNone() throws Exception {
		
		createEnigne();
		
		try { // none - try stopped after retry
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_none.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=none";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				boolean found = false;
				for (PNodeInfo nodeId : engine.storageGetFlowNodes(caseId, STATE_NODE.RUNNING)) {
					PNode node = engine.getNodeWithoutLock(nodeId.getId());
					found = true;
					node.setScheduled(System.currentTimeMillis());
					config.storage.saveFlowNode(node);
				}
				if (!found) break;
			}
			assertTrue(i < 10);
			mockup.close();
            try (PCaseLock lock = engine.getCaseLock(caseId)) {
                lock.closeCase(true, -1, "test");
            }			
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

		archiveEngine(engine, config);
	}
	
    @Test
	public void testStartpoint() throws Exception {
		
		createEnigne();
		
		try { // custom startpoint
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_startpoint.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool#de.mhus.cherry.reactive.examples.simple1.S1Start2";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze = engine.getCaseWithoutLock(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
		
		archiveEngine(engine, config);
	}
	
    @Test
	public void testExternal() throws Exception {
		
		createEnigne();

		try { // fire external
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_fireexternal.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=external";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			boolean found = false;
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();

				if (i == 5) {
					for (PNodeInfo node : engine.storageGetFlowNodes(caseId, STATE_NODE.WAITING)) {
						if (node.getType() == TYPE_NODE.EXTERN) {
							engine.fireExternal(node.getId(), null, new MProperties());
							found = true;
						}
					}
				}
				
				PCase caze = engine.getCaseWithoutLock(caseId);
				if (caze.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(i < 10);
			assertTrue(found);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
		
		archiveEngine(engine, config);
	}
	
    @Test
	public void testSignal() throws Exception {
		
		createEnigne();
		
		try { // fire signal
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_firesignal.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=signal";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId1 = engine.start(uri);
			UUID caseId2 = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			boolean found = false;
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();

				if (i == 5) {
					for (PNodeInfo node : engine.storageGetFlowNodes(caseId1, STATE_NODE.WAITING)) {
						if (node.getType() == TYPE_NODE.SIGNAL) {
							int cnt = engine.fireSignal("signal", new MProperties());
							assertEquals(2, cnt);
							found = true;
						}
					}
				}
				
				PCase caze1 = engine.getCaseWithoutLock(caseId1);
				PCase caze2 = engine.getCaseWithoutLock(caseId2);
				if (caze1.getState() == STATE_CASE.CLOSED && caze2.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(found);
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
		
		archiveEngine(engine, config);
	}
	
    @Test
	public void testMessage() throws Exception {
		
		createEnigne();

		try { // fire message
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_firemessage.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=message";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId1 = engine.start(uri);
			UUID caseId2 = engine.start(uri);
			System.out.println("CASE1 " + caseId1);
			System.out.println("CASE2 " + caseId2);
			printStorage();
			mockup.step();
			
			boolean found = false;
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
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
				
				PCase caze1 = engine.getCaseWithoutLock(caseId1);
				PCase caze2 = engine.getCaseWithoutLock(caseId2);
				if (caze1.getState() == STATE_CASE.CLOSED && caze2.getState() == STATE_CASE.CLOSED) break;
			}
			assertTrue(found);
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
		
		archiveEngine(engine, config);
	}
	
    @Test
	public void testExclusiveGateway() throws Exception {
		
		createEnigne();

		try { // exclusive kirk
			EngineMockUp mockup = new EngineMockUp(config.storage, engine, new File("mockup/s1_exclusivekirk.xml"));
			mockup.setWarn(false);
			mockup.setVerbose(false);
			String uri = "bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool?text1=exclusive&text2=kirk";
			System.out.println("URI: " + uri);
			System.out.println("------------------------------------------------------------------------");
			UUID caseId1 = engine.start(uri);
			
			printStorage();
			mockup.step();
			
			int i = 0;
			for (i = 1; i <= 10; i++) {
				step(i);
				mockup.step();
				
				PCase caze1 = engine.getCaseWithoutLock(caseId1);
				if (caze1.getState() == STATE_CASE.CLOSED) {
					assertEquals(2, caze1.getClosedCode());
					break;
				}
			}
			assertTrue(i < 10);
			mockup.close();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
		
		archiveEngine(engine, config);

	}
		
	private void createEnigne() throws MException {
		
		console = Console.get();
		console.setBold(true);
		console.setColor(COLOR.RED, null);
		System.out.println("========================================================================================");
		System.out.println(MSystem.findSourceMethod(3));
		System.out.println("========================================================================================");
		console.cleanup();
		File f = new File("target/classes");
		System.out.println(f.getAbsolutePath());
		DefaultProcessLoader loader = new DefaultProcessLoader(new File[] {f});
		DefaultProcessProvider provider = new DefaultProcessProvider();
		provider.addProcess(loader);

		config = new EngineConfiguration();
		config.storage = new MemoryStorage();
		config.archive = new MemoryStorage();
		config.aaa = new SimpleAaaProvider();
		config.parameters = new HashMap<>();
		config.parameters.put("process:de.mhus.cherry.reactive.examples.simple1.S1Process:versions", "0.0.1");
		config.executeParallel = false;
		
		config.listener.add(EngineListenerUtil.createAnsiListener());
		
		config.processProvider = provider;
		
		engine = new Engine(config);

	}
	
	private void archiveEngine(Engine engine, EngineConfiguration config) throws IOException, MException {
		
		// find runtime
		for (PNodeInfo nodeId : engine.storageGetFlowNodes(null, null)) {
			PNode node = engine.getNodeWithoutLock(nodeId.getId());
			if (node.getType() == TYPE_NODE.RUNTIME) {
				PCase caze = engine.getCaseWithoutLock(node.getCaseId());
				EngineContext context = engine.createContext(caze);
				RuntimeNode runtime = engine.createRuntimeObject(context, node);
				RuntimeTrace trace = new RuntimeTrace(runtime);
				console.setBold(true);
				console.setColor(COLOR.RED, null);
				System.out.println("Runtime protocol: " + node.getId() + " " + node.getState() + " " + node.getCaseId());
				console.cleanup();
				trace.dumpMessages(System.out);
			}
		}
		
		engine.archiveAll();
		
		printStorage();

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
	
	private void step(int i) throws InterruptedException, NotFoundException, IOException {
		Thread.sleep(sleep);
		System.out.println();
		console.setBold(true);
		console.setColor(COLOR.GREEN, null);
		System.out.println("------------------------------------------------------------------------");
		System.out.println(MSystem.findSourceMethod(3) + " Step " + i);
		System.out.println("------------------------------------------------------------------------");
		console.cleanup();
		System.out.flush();
		engine.step();
		printStorage();
	}

	public void printStorage() {
		console.setColor(COLOR.BLUE, null);
		try {
			for (PCaseInfo info : config.storage.getCases(null)) {
				System.out.println("CASE: " + info.getState() + " " + info.getCanonicalName() + " " + info.getId() + " " + info.getUri());
			}
		} catch (IOException e) {
		}
		console.setColor(COLOR.YELLOW, null);
		try {
			for (PNodeInfo info : config.storage.getFlowNodes(null,null)) {
				if (info.getState() != STATE_NODE.CLOSED)
					System.out.println("NODE: " + info.getState() + " " + info.getType() + " " + info.getCanonicalName() + " " + info.getId() + " " + info.getCaseId());
			}
		} catch (IOException e) {
		}
		console.cleanup();
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
