package io.quarkus.maven;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "run")
public class RunMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Process app = null;
        try {
            app = new ProcessBuilder()
                    .command("java", "-jar", "target/quarkus-app/quarkus-run.jar")
                    .inheritIO()
                    .redirectErrorStream(true)
                    .start();
            app.waitFor();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to launch the application ｡ﾟ(ﾟஇ‸இﾟ)ﾟ｡", e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Application process was interrupted", e);
        }
    }
}
