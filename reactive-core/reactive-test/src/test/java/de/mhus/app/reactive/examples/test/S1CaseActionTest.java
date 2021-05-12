package de.mhus.app.reactive.examples.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.mhus.app.reactive.engine.Engine;
import de.mhus.app.reactive.engine.EngineConfiguration;
import de.mhus.app.reactive.engine.util.DefaultProcessLoader;
import de.mhus.app.reactive.engine.util.EngineListenerUtil;
import de.mhus.app.reactive.engine.util.JavaPackageProcessProvider;
import de.mhus.app.reactive.model.engine.EngineConst;
import de.mhus.app.reactive.util.engine.MemoryStorage;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.Console.COLOR;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.node.INode;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.ModelUtil;

public class S1CaseActionTest {

    
    private EngineConfiguration config;
    private Engine engine;
    private Console console;

    @Test
    public void testAcions() throws Exception {

        createEngine();

        String uri =
                "bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool?text1=area&testDate=1.2.1997&testEnum=ON&testInt=5&testInteger=17";
        System.out.println("URI: " + uri);
        System.out.println(
                "------------------------------------------------------------------------");
        UUID caseId = engine.start(uri);

        {
            MProperties actions = engine.onUserCaseAction(caseId, "actions", null);
            
            assertTrue(actions.containsKey("action"));
            
            MProperties res = engine.onUserCaseAction(caseId, "action", null);
            
            assertEquals("b", res.getString("a"));
        }
        {
            MProperties actions = engine.onUserCaseAction(caseId, EngineConst.ACTION_LIST, null);
            System.out.println(actions);
            assertTrue(actions.containsKey("test"));
            assertEquals("Test", actions.get("test"));
        }
        {
            MProperties values = new MProperties("action","test");
            MProperties form = engine.onUserCaseAction(caseId, EngineConst.ACTION_FORM, values);
            System.out.println(form);
            assertNotNull(form);
            String formStr = form.getString("form");
            DefRoot model = ModelUtil.fromJson(formStr);
            assertNotNull(model);
            System.out.println(model);
            int cnt = 0;
            for (INode entry : model.getArray(INode.NAMELESS_VALUE)) {
                if (cnt == 0) {
                    assertEquals("name", entry.getString("name"));
                    assertEquals("name.title=Name", entry.getString("caption"));
                }
                cnt++;
            }
         //   elements = model.getArray("element");
        }
    }
    
    private void createEngine() throws MException, IOException {

        console = Console.get();
        console.setBold(true);
        console.setColor(COLOR.RED, null);
        System.out.println(
                "========================================================================================");
        System.out.println(MSystem.findCallingMethod(3));
        System.out.println(
                "========================================================================================");
        console.cleanup();
        File f = new File("target/classes");
        System.out.println(f.getAbsolutePath());
        DefaultProcessLoader loader = new DefaultProcessLoader(new File[] {f});
        JavaPackageProcessProvider provider = new JavaPackageProcessProvider();
        provider.addProcess(loader);

        config = new EngineConfiguration();
        config.storage = new MemoryStorage();
        config.archive = new MemoryStorage();
        config.aaa = new SimpleAaaProvider();
        config.parameters = new HashMap<>();
        config.parameters.put(
                "process:de.mhus.app.reactive.examples.simple1.S1Process:versions", "0.0.1");
        config.executeParallel = false;

        config.listener.add(EngineListenerUtil.createAnsiListener());

        config.processProvider = provider;

        engine = new Engine(config);
    }
}
