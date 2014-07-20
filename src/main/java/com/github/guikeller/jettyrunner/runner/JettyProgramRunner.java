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
 * Created by Gui on 14/07/2014.
 */
public class JettyProgramRunner extends DefaultProgramRunner {

    public JettyProgramRunner(){
        super();
    }

    @NotNull
    public String getRunnerId() {
        return "JettyRunner-GK";
    }

    public boolean canRun(@NotNull String value, @NotNull RunProfile runProfile) {
        if(!(runProfile instanceof JettyRunnerConfiguration)){
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
