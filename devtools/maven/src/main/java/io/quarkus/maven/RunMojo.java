package io.quarkus.maven;

import java.io.File;
import java.io.IOException;
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
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Process app = null;
        try {
            app = new ProcessBuilder()
                    .command("java", "-jar", "target/quarkus-app/quarkus-run.jar") //getAbsolutePath()?
                    .inheritIO()
                    .redirectErrorStream(true)
                    .start();
                    /*.directory(workingDir == null ? project.getBasedir() : workingDir);
            if (!environmentVariables.isEmpty()) {
                processBuilder.environment().putAll(environmentVariables);
            } */
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
                    getLog().warn("Unable to properly wait for dev-mode end", e);
                }
            }
        }, "Development Mode Shutdown Hook"));
        void stop() throws InterruptedException {
            process.destroy();
            process.waitFor();
        }
    }
}
