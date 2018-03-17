package de.mhus.cherry.reactive.osgi.impl;

import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;

public class OsgiProcessLoader implements ProcessLoader {

	private AProcess process;

	public OsgiProcessLoader(AProcess process) {
		this.process = process;
		init();
	}

	protected void init() {
		Bundle bundle = FrameworkUtil.getBundle(process.getClass());
		LinkedList<String> classNames = new LinkedList<>();
		// TODO iterate and parse .... like DefaultProcesLoader
	}

	@Override
	public List<Class<? extends AElement<?>>> getElements() {
		// TODO Auto-generated method stub
		return null;
	}

}
