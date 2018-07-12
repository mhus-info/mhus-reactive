package de.mhus.cherry.reactive.maven;

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

@Mojo(name="test")
public class TestMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;
	
	@Component
    private MavenProjectHelper projectHelper;
	
	@Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;

    @Parameter( defaultValue = "${project.compileClasspathElements}", readonly = true, required = true )
    private List<String> compilePath;
        
	@SuppressWarnings("unchecked")
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
			
			for (Dependency item : (List<Dependency>)project.getDependencies()) {
				File f = locateDependency(item);
				getLog().warn("Dependencies: " + item.getScope() + " " + f);
				
			}
			
			
			Collection<File> dependencyArtifacts = getCompileClasspathElements();
            			
			for (File path : dependencyArtifacts)
				getLog().warn("Path: " + path);
				
			
			for (String item :compilePath)
				getLog().warn("CompilePath: " + item);

//			session.getCurrentProject().getDependencyManagement()
			
		} catch (Exception e) {
			getLog().error(e);
		}
	}

	private File locateDependency(Dependency item) {
		return MMaven.locateArtifact(MMaven.toArtifact(item.getGroupId(), item.getArtifactId(), item.getVersion(), item.getType()));
	}

	@SuppressWarnings("unchecked")
	private List<File> getCompileClasspathElements( )
    {
        List<File> list = new ArrayList<File>( project.getArtifacts().size() + 1 );

        list.add( new File( project.getBuild().getOutputDirectory() ) );

        for ( Artifact a : (Set<Artifact>)project.getArtifacts() )
        {
            list.add( a.getFile() );
        }
        return list;
    }
}
