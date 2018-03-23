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

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;

public class DefaultProcessLoader extends MLog implements ProcessLoader {
	
	protected LinkedList<URL> classLoaderUrls = new LinkedList<>();
	protected LinkedList<Class<? extends AElement<?>>> elementClasses = new LinkedList<>();
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
				File file = new File(url.getFile());
				if (file.isDirectory() && file.getName().equals("classes")) {
					findClasses(file, classNames, file.getAbsolutePath());
				} else
				if (file.isFile() && file.getName().endsWith(".jar")) {
					JarFile jar = new JarFile(file);
					for (Enumeration<JarEntry> enu = jar.entries();enu.hasMoreElements();) {
						JarEntry entry = enu.nextElement();
						String name = entry.getName();
						if (name.endsWith(".class") && name.indexOf('$') < 0) {
							if (name.startsWith("/")) name = name.substring(1);
							classNames.add(MString.beforeLastIndex(name,'.').replace('/', '.'));
						}
					}
					jar.close();
				}
			} catch (Throwable t) {
				log().w(url,t);
			}
		}
		
		// load class and test if it's Element
		for (String name : classNames) {
			try {
				Class<?> clazz = classLoader.loadClass(name);
				if (AElement.class.isAssignableFrom(clazz))
					elementClasses.add((Class<? extends AElement<?>>) clazz);
			} catch (Throwable t) {
				log().w(name,t);
			}
		}
		
	}

	private void findClasses(File dir, LinkedList<String> classNames, String base) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory() && !file.getName().startsWith("."))
				findClasses(file, classNames, base);
			else
			if (file.isFile() && file.getName().endsWith(".class")) {
				String name = file.getAbsolutePath().substring(base.length());
				if (name.startsWith("/")) name = name.substring(1);
				classNames.add(MString.beforeLastIndex(name,'.').replace('/', '.'));
			}
		}
	}

	@SuppressWarnings("deprecation")
	protected void load(File dir) {
		if (dir.isDirectory() && dir.getName().equals("classes")) {
			try {
				classLoaderUrls.add( dir.toURL() );
			} catch (MalformedURLException e) {
				log().w(dir,e);
			}
		} else
		if (dir.isDirectory()) {
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
		} else
		if (dir.isFile() && dir.getName().endsWith(".jar")) {
			try {
				classLoaderUrls.add( dir.toURL() );
			} catch (MalformedURLException e) {
				log().w(dir,e);
			}
		}
	}

	@Override
	public List<Class<? extends AElement<?>>> getElements() {
		return Collections.unmodifiableList(elementClasses);
	}

}
