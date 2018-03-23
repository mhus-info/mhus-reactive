package de.mhus.cherry.reactive.osgi.impl;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;

public class OsgiProcessLoader extends MLog implements ProcessLoader {

	protected LinkedList<Class<? extends AElement<?>>> elementClasses = new LinkedList<>();
	private AProcess process;

	public OsgiProcessLoader(AProcess process) {
		this.process = process;
		init();
	}

	@SuppressWarnings("unchecked")
	protected void init() {
		Bundle bundle = FrameworkUtil.getBundle(process.getClass());
		LinkedList<String> classNames = new LinkedList<>();
		// TODO iterate and parse .... like DefaultProcesLoader
		String start = process.getClass().getPackage().getName().replace('.', '/');
		Enumeration<URL> enu = bundle.findEntries(start, "*.class", true);
		while (enu.hasMoreElements()) {
			URL url = enu.nextElement();
			String name = url.getPath();
			if (name.endsWith(".class") && name.indexOf('$') < 0) {
				if (name.startsWith("/")) name = name.substring(1);
				classNames.add(MString.beforeLastIndex(name,'.').replace('/', '.'));
			}
		}
		
		// load class and test if it's Element
		for (String name : classNames) {
			try {
				Class<?> clazz = bundle.loadClass(name);
				if (AElement.class.isAssignableFrom(clazz))
					elementClasses.add((Class<? extends AElement<?>>) clazz);
			} catch (Throwable t) {
				log().w(name,t);
			}
		}

	}

	@Override
	public List<Class<? extends AElement<?>>> getElements() {
		return Collections.unmodifiableList(elementClasses);
	}

}
