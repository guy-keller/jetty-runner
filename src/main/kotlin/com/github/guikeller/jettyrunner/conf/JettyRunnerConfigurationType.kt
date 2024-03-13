package com.github.guikeller.jettyrunner.conf

import com.github.guikeller.jettyrunner.util.IconUtil
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import javax.swing.Icon

/**
 * Jetty Runner Configuration Type
 * @see com.intellij.execution.configurations.ConfigurationType
 *
 * @author Guy Keller
 */
class JettyRunnerConfigurationType : ConfigurationType {
    override fun getDisplayName(): String {
        return "Jetty-Runner"
    }

    override fun getConfigurationTypeDescription(): String {
        return "Jetty-Runner"
    }

    override fun getIcon(): Icon {
        return IconUtil.getInstance().getIcon()
    }

    override fun getId(): String {
        return "Jetty-Runner"
    }

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        val factory = JettyRunnerConfigurationFactory(this)
        return arrayOf(factory)
    }
}
