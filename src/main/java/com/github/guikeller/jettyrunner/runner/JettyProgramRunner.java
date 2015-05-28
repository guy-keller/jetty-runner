package com.github.guikeller.jettyrunner.runner;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * Jetty Program Runner
 * @see com.intellij.execution.runners.DefaultProgramRunner
 * @author Gui Keller
 */
public class JettyProgramRunner extends DefaultProgramRunner {

    private static final String RUN = "Run";

    public JettyProgramRunner(){
        super();
    }

    @NotNull
    public String getRunnerId() {
        return "JettyRunner-By-GuiKeller";
    }

    @Override
    public boolean canRun(@NotNull String value, @NotNull RunProfile runProfile) {
        // It can only run JettyRunnerConfigurations
        if(!(runProfile instanceof JettyRunnerConfiguration)){
            return false;
        }
        // Values passed are: Run or Debug
        if(!RUN.equals(value)) {
            // Fallback on the JettyProgramDebugger
            return false;
        }
        return true;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state,
                                             @NotNull ExecutionEnvironment env) throws ExecutionException {
        return super.doExecute(state, env);
    }
}
