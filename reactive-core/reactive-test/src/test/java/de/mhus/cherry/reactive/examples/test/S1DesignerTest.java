package de.mhus.cherry.reactive.examples.test;

import java.io.File;

import de.mhus.cherry.reactive.engine.util.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.util.designer.DesignerUtil;
import de.mhus.cherry.reactive.util.designer.XmlModel;
import junit.framework.TestCase;

public class S1DesignerTest extends TestCase {

	public void testCreateDesign() throws Exception {
		File f = new File("target/classes");
		System.out.println(f.getAbsolutePath());
		DefaultProcessLoader loader = new DefaultProcessLoader(new File[] {f});
		DefaultProcessProvider provider = new DefaultProcessProvider();
		provider.addProcess(loader);

		XmlModel model = new XmlModel();
		EProcess process = provider.getProcess("de.mhus.cherry.reactive.examples.simple1.S1Process", "0.0.1");
		EPool pool = process.getPool("de.mhus.cherry.reactive.examples.simple1.S1Pool");
		model.merge(process, pool);
		
		model.dump();
		
		DesignerUtil.createDocument(model, new File("target/S1Pool-1.bpmn2") );
		DesignerUtil.createDocument(model, new File("target/S1Pool-3.bpmn2"));

		// try loading again
		model = new XmlModel();
		DesignerUtil.load(model, new File("target/S1Pool-1.bpmn2"));
		// save again
		DesignerUtil.createDocument(model, new File("target/S1Pool-2.bpmn2"));
		
		// merge in existing
		DesignerUtil.saveInto(model, new File("target/S1Pool-3.bpmn2"));
		
		// merge in existing
		DesignerUtil.saveInto(model, new File("target/S1Pool-4.bpmn2"));
		
	}
}
