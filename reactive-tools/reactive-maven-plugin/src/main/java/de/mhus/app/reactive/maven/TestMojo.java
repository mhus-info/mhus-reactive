/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.reactive.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import de.mhus.lib.core.util.MMaven;

@Mojo(name = "test")
public class TestMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Component private MavenProjectHelper projectHelper;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(
            defaultValue = "${project.compileClasspathElements}",
            readonly = true,
            required = true)
    private List<String> compilePath;

    @SuppressWarnings("deprecation")
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {
            for (Object item : project.getCompileClasspathElements()) {
                String path = String.valueOf(item);
                getLog().warn("CompileClasspathElements: " + path);
            }
            for (Object item : project.getTestClasspathElements()) {
                String path = String.valueOf(item);
                getLog().warn("TestClasspathElements: " + path);
            }
            for (Object item : project.getRuntimeDependencies()) {
                getLog().warn("RuntimeDependencies: " + item);
            }
            for (Object item : project.getTestDependencies()) {
                getLog().warn("TestDependencies: " + item.getClass() + " " + item);
            }

            for (Dependency item : (List<Dependency>) project.getDependencies()) {
                File f = locateDependency(item);
                getLog().warn("Dependencies: " + item.getScope() + " " + f);
            }

            Collection<File> dependencyArtifacts = getCompileClasspathElements();

            for (File path : dependencyArtifacts) getLog().warn("Path: " + path);

            for (String item : compilePath) getLog().warn("CompilePath: " + item);

            //			session.getCurrentProject().getDependencyManagement()

        } catch (Exception e) {
            getLog().error(e);
        }
    }

    private File locateDependency(Dependency item) {
        return MMaven.locateArtifact(
                MMaven.toArtifact(
                        item.getGroupId(),
                        item.getArtifactId(),
                        item.getVersion(),
                        item.getType()));
    }

    private List<File> getCompileClasspathElements() {
        List<File> list = new ArrayList<File>(project.getArtifacts().size() + 1);

        list.add(new File(project.getBuild().getOutputDirectory()));

        for (Artifact a : (Set<Artifact>) project.getArtifacts()) {
            list.add(a.getFile());
        }
        return list;
    }
}
