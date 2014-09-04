package com.github.guikeller.jettyrunner.runner;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Jetty Program Debugger - Jetty Runner on Debug mode or Normal mode
 * @author Gui Keller
 */
public class JettyProgramDebugger extends GenericDebuggerRunner {

    // These are JVM 6 onwards hence the JVM6 as min requirement
    private static final String XDEBUG = "-Xdebug";
    private static final String JDWP = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=";

    private static final String LOCALHOST = "127.0.0.1";


    public JettyProgramDebugger(){
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
        return true;
    }

    @Nullable
    @Override
    protected RunContentDescriptor createContentDescriptor(Project project, RunProfileState state, RunContentDescriptor contentToReuse, ExecutionEnvironment env)
                throws ExecutionException {
        // Now we figure out if it the Debug button has been hit
        Executor executor = env.getExecutor();
        // If was the debug, then we do some extra magic
        if(executor instanceof DefaultDebugExecutor) {
            // Get hold of the JavaParameters
            JavaCommandLine javaCommandLine = (JavaCommandLine) state;
            JavaParameters javaParameters = javaCommandLine.getJavaParameters();
            // Making the assumption that it's JVM 6.0 onwards
            javaParameters.getVMParametersList().addParametersString(XDEBUG);
            // Debugger port
            String debuggerPort = DebuggerUtils.getInstance().findAvailableDebugAddress(true);
            String remotePort = JDWP + debuggerPort;
            javaParameters.getVMParametersList().addParametersString(remotePort);
            // Creating a 'Remote' configuration on the fly
            RemoteConnection connection = new RemoteConnection(true, LOCALHOST, debuggerPort, false);
            // Attaches the remote configuration to the VM and then starts it up
            return attachVirtualMachine(project, state, contentToReuse, env, connection, true);
        }else{
            // If it was something else then we don't do anything special
            return super.createContentDescriptor(project,state,contentToReuse,env);
        }
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull Project project, @NotNull RunProfileState state, RunContentDescriptor contentToReuse, @NotNull ExecutionEnvironment env) throws ExecutionException {
        return super.doExecute(project, state, contentToReuse, env);
    }

}
