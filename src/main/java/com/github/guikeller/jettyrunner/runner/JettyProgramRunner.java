package com.github.guikeller.jettyrunner.runner;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import org.jetbrains.annotations.NotNull;

/**
 * Jetty Program Runner - Default / Run
 * @see com.intellij.execution.impl.DefaultJavaProgramRunner
 * @author Gui Keller
 */
public class JettyProgramRunner extends DefaultJavaProgramRunner {

    public JettyProgramRunner(){
        super();
    }

    @Override
    @NotNull
    public String getRunnerId() {
        return "JettyRunner-By-GuiKeller";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile runProfile) {
        // It can only run JettyRunnerConfigurations
        return executorId.equals(DefaultRunExecutor.EXECUTOR_ID) && runProfile instanceof JettyRunnerConfiguration;
    }

}
