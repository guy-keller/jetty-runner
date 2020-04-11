package com.github.guikeller.jettyrunner.conf;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Jetty Runner Configuration Factory
 * @see com.intellij.execution.configurations.ConfigurationFactory
 * @author Gui Keller
 */
public class JettyRunnerConfigurationFactory extends ConfigurationFactory {

    public JettyRunnerConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new JettyRunnerConfiguration(project, this, "Jetty Runner");
    }

    @Override
    public RunConfigurationSingletonPolicy getSingletonPolicy() {
        return RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY;
    }

}
