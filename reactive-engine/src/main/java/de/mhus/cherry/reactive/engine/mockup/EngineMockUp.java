package de.mhus.cherry.reactive.engine.mockup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.StorageProvider;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.XmlConfig;
import de.mhus.lib.core.config.XmlConfigFile;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public class EngineMockUp {

	private StorageProvider storage;
	private Engine engine;
	private LinkedList<Step> steps = new LinkedList<>();
	private File file;
	private boolean recording;
	private int cnt;
	private boolean warn = false;

	public EngineMockUp(StorageProvider storage, Engine engine, File file) throws FileNotFoundException, Exception {
		this.storage = storage;
		this.engine = engine;
		this.file = file;
		recording = !file.exists();
		if (!recording)
			load();
	}
	
	public void step() throws NotFoundException, IOException {
		if (recording)
			record();
		else
			play();
	}
	
	protected void play() throws NotFoundException, IOException {
		Step step = steps.removeFirst();
		cnt++;
		if (step.getNr() != cnt) throw new IOException("Wrong Step Number " + step.getNr());
		for (PCaseInfo info : storage.getCases(null)) {
			PCase caze = storage.loadCase(info.getId());
			step.check(cnt,caze);
		}
		for (PNodeInfo info : storage.getFlowNodes(null, null)) {
			PNode node = storage.loadFlowNode(info.getId());
			step.check(warn,cnt,node);
		}
	}
	
	protected void record() throws NotFoundException, IOException {
		Step step = new Step();
		for (PCaseInfo info : storage.getCases(null)) {
			PCase caze = storage.loadCase(info.getId());
			step.add(caze);
		}
		for (PNodeInfo info : storage.getFlowNodes(null, null)) {
			PNode node = storage.loadFlowNode(info.getId());
			step.add(node);
		}
		
		steps.add(step);
	}
	
	public void close() throws Exception {
		if (recording)
			save();
	}
	
	protected void load() throws FileNotFoundException, Exception {
		XmlConfigFile config = new XmlConfigFile(file);
		steps.clear();
		for (IConfig nstep : config.getNodes("step")) {
			Step step = new Step(nstep);
			steps.add(step);
		}
	}
	
	protected void save() throws Exception {
		cnt = 0;
		XmlConfig config = new XmlConfig();
		for (Step step : steps) {
			cnt++;
			IConfig child = config.createConfig("step");
			step.save(cnt,child);
		}
		MXml.saveXml(config.getXmlElement(), file);
	}

	public boolean isWarn() {
		return warn;
	}

	public void setWarn(boolean warn) {
		this.warn = warn;
	}
	
}
