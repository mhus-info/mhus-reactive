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
package de.mhus.cherry.reactive.maven;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import de.mhus.cherry.reactive.engine.util.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.lib.core.util.MMaven;
import de.mhus.lib.errors.MException;

public abstract class AbstractReactiveMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

	@SuppressWarnings("unchecked")
	public DefaultProcessProvider createProvider() throws DependencyResolutionRequiredException, MException {
		
		LinkedList<File> files = new LinkedList<>();
		LinkedList<File> search = new LinkedList<>();
		for (Dependency item : (List<Dependency>)project.getDependencies()) {
			File f = locateDependency(item);
			getLog().debug("Dependency: " + item.getScope() + " " + f);
			files.add(f);
		}
					
		for (Object item : project.getTestClasspathElements()) {
			String path = String.valueOf(item);
			getLog().debug("TestClasspathElement: " + path);
			File f= new File(path);
			search.add(f);
		}
		
		DefaultProcessLoader loader = new DefaultProcessLoader(files.toArray(new File[files.size()]), search.toArray(new File[search.size()]), null);
		DefaultProcessProvider provider = new DefaultProcessProvider();
		provider.addProcess(loader);

		return provider;

	}

	private File locateDependency(Dependency item) {
		return MMaven.locateArtifact(MMaven.toArtifact(item.getGroupId(), item.getArtifactId(), item.getVersion(), item.getType()));
	}

}
