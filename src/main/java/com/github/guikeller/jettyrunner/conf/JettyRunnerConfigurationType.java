package com.github.guikeller.jettyrunner.conf;

import com.github.guikeller.jettyrunner.util.IconUtil;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * Created by Gui Keller on 12/07/2014.
 */
public class JettyRunnerConfigurationType implements ConfigurationType {

    public JettyRunnerConfigurationType(){
        super();
    }

    public String getDisplayName() {
        return "Jetty Runner";
    }

    public String getConfigurationTypeDescription() {
        return "IntelliJ IDEA Jetty (Basic) Runner";
    }

    public Icon getIcon() {
        return IconUtil.getInstance().getIcon();
    }

    @NotNull
    public String getId() {
        return "JettyRunner-GK";
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        JettyRunnerConfigurationFactory factory = new JettyRunnerConfigurationFactory(this);
        return new ConfigurationFactory[]{factory};
    }
}
