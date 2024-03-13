package com.github.guikeller.jettyrunner.conf

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy
import com.intellij.openapi.project.Project

/**
 * Jetty Runner Configuration Factory
 * @see com.intellij.execution.configurations.ConfigurationFactory
 *
 * @author Guy Keller
 */
class JettyRunnerConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String {
        return super.getName()
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return JettyRunnerConfiguration(project, this, "Jetty-Runner")
    }

    override fun getSingletonPolicy(): RunConfigurationSingletonPolicy {
        return RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY
    }
}
