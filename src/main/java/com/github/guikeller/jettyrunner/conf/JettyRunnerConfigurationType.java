package com.github.guikeller.jettyrunner.conf;

import com.github.guikeller.jettyrunner.util.IconUtil;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Jetty Runner Configuration Type
 * @see com.intellij.execution.configurations.ConfigurationType
 * @author Gui Keller
 */
public class JettyRunnerConfigurationType implements ConfigurationType {

    public JettyRunnerConfigurationType(){
        super();
    }

    @Override
    public String getDisplayName() {
        return "Jetty Runner";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "IntelliJ IDEA Jetty Runner";
    }

    @Override
    public Icon getIcon() {
        return IconUtil.getInstance().getIcon();
    }

    @Override
    @NotNull
    public String getId() {
        return "JettyRunner-By-GuiKeller";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        JettyRunnerConfigurationFactory factory = new JettyRunnerConfigurationFactory(this);
        return new ConfigurationFactory[]{factory};
    }
}
