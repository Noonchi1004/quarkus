package io.quarkus.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.jar.JarException;
import java.util.jar.JarFile;

import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.shared.utils.cli.CommandLineUtils;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;


@Mojo(name = "run")
public class RunMojo extends AbstractMojo {
    private Process process;
    protected MavenProject project;
    private File outputDirectory;
    private Path path;

    public boolean isPathValid() {
        return Files.exists(Paths.get(project.getBuild().getDirectory()));
}

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Process app = null;
        if (!isPathValid()) {
           throw new MojoExecutionException("Failed to find valid file Path"); //add more precise comment + file
        }
        try {
            app = new ProcessBuilder()
                    .command("JavaBinFinder.findBin()", "-jar", "target/quarkus-app/quarkus-run.jar") //getAbsolutePath()?
                    .inheritIO()
                    .redirectErrorStream(true)
                    .start();
            app.waitFor();

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to launch the application ｡ﾟ(ﾟஇ‸இﾟ)ﾟ｡", e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Application process was interrupted", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                process.destroy();
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    getLog().warn("Unable to properly wait for run-mode end", e);
                }
            }
        }, "Development Mode Shutdown Hook"));
        /*void stop() throws InterruptedException { //check errors
            process.destroy();
            process.waitFor();
        }*/
    }
}
