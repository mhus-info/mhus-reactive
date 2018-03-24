package de.mhus.cherry.reactive.engine;

import java.util.LinkedList;
import java.util.Map;

import de.mhus.cherry.reactive.model.engine.AaaProvider;
import de.mhus.cherry.reactive.model.engine.EngineListener;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.ProcessProvider;
import de.mhus.cherry.reactive.model.engine.StorageProvider;

public class EngineConfiguration {

	public StorageProvider storage;
	public StorageProvider archive;
	public ProcessProvider processProvider;
	public AaaProvider aaa;
	public Map<String,Object> parameters;
	public LinkedList<EngineListener> listener = new LinkedList<>();
	public PEngine persistent;
	
}
