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
/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
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
