package de.mhus.cherry.reactive.engine;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.mhus.cherry.reactive.model.activity.Element;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;

public class DefaultProcessLoader extends MLog implements ProcessLoader {
	
	protected LinkedList<URL> classLoaderUrls = new LinkedList<>();
	protected LinkedList<Class<? extends Element>> elementClasses = new LinkedList<>();
	protected URLClassLoader classLoader;

	public DefaultProcessLoader(File[] dirs) {
		for (File dir : dirs)
			load(dir);
		init();
	}
	
	@SuppressWarnings("unchecked")
	protected void init() {
		classLoader = new URLClassLoader(classLoaderUrls.toArray(new URL[classLoaderUrls.size()]), getClass().getClassLoader());

		LinkedList<String> classNames = new LinkedList<>();
		// load from jar files
		for (URL url : classLoaderUrls) {
			try {	
				JarFile jar = new JarFile(url.getFile());
				for (Enumeration<JarEntry> enu = jar.entries();enu.hasMoreElements();) {
					JarEntry entry = enu.nextElement();
					String name = entry.getName();
					if (name.endsWith(".class") && name.indexOf('$') < 0) {
						if (name.startsWith("/")) name = name.substring(1);
						classNames.add(MString.beforeLastIndex(name,'.').replace('/', '.'));
					}
				}
				jar.close();
			} catch (Throwable t) {
				log().w(url,t);
			}
		}
		
		// load class and test if it's Element
		for (String name : classNames) {
			try {
				Class<?> clazz = classLoader.loadClass(name);
				if (Element.class.isAssignableFrom(clazz))
					elementClasses.add((Class<? extends Element>) clazz);
			} catch (Throwable t) {
				log().w(name,t);
			}
		}
		
	}

	@SuppressWarnings("deprecation")
	protected void load(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				if (file.getName().endsWith(".jar"))
					try {
						classLoaderUrls.add( file.toURL() );
					} catch (MalformedURLException e) {
						log().w(file,e);
					}
			}
		}
	}

	@Override
	public List<Class<? extends Element>> getElements() {
		return Collections.unmodifiableList(elementClasses);
	}

}
