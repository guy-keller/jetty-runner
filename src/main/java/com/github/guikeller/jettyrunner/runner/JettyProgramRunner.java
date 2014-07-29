package com.github.guikeller.jettyrunner.runner;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Jetty Program Runner - Boilerplate
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

    public boolean canRun(@NotNull String value, @NotNull RunProfile runProfile) {
        if(!(runProfile instanceof JettyRunnerConfiguration)){
            return false;
        }
        if(!RUN.equals(value)) {
            return false;
        }
        return true;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull Project project,
                                             @NotNull RunProfileState state,
                                             RunContentDescriptor contentToReuse,
                                             @NotNull ExecutionEnvironment env) throws ExecutionException {
        return super.doExecute(project, state, contentToReuse, env);
    }
}
