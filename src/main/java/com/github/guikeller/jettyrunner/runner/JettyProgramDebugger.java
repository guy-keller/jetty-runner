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
import org.jetbrains.annotations.NotNull;

/**
 * Jetty Program Debugger - Jetty Runner on Debug mode
 * @see com.intellij.debugger.impl.GenericDebuggerRunner
 * @author Gui Keller
 */
public class JettyProgramDebugger extends GenericDebuggerRunner {

    // These are JVM 8 onwards hence why the JVM 8 as min requirement
    private static final String XDEBUG = "-Xdebug";
    private static final String JDWP = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=";

    private static final String LOCALHOST = "127.0.0.1";

    public JettyProgramDebugger(){
        super();
    }

    @Override
    @NotNull
    public String getRunnerId() {
        return "JettyRunner-By-GuiKeller";
    }

    @Override
    public boolean canRun(@NotNull String value, @NotNull RunProfile runProfile) {
        // It can only run JettyRunnerConfigurations
        return runProfile instanceof JettyRunnerConfiguration;
    }

    @Override
    protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state,
                                                           @NotNull ExecutionEnvironment environment) throws ExecutionException {
        // Now we figure out if it the Debug button has been hit
        Executor executor = environment.getExecutor();
        // If was the debug, then we do some extra magic
        if(executor instanceof DefaultDebugExecutor) {
            String debuggerPort = DebuggerUtils.getInstance().findAvailableDebugAddress(true);
            // Get hold of the JavaParameters
            RunProfileState stateWithDebug = addParamsToJavaCmdLine(state, debuggerPort);
            // Creating a 'Remote' configuration on the fly
            RemoteConnection connection = new RemoteConnection(true, LOCALHOST, debuggerPort, false);
            // Attaches the remote configuration to the VM and then starts it up
            return super.attachVirtualMachine(stateWithDebug, environment, connection, true);
        }else{
            // If it was something else then we don't do anything special
            return super.createContentDescriptor(state, environment);
        }
    }

    protected RunProfileState addParamsToJavaCmdLine(RunProfileState state, String debuggerPort) throws  ExecutionException {
        JavaCommandLine javaCommandLine = (JavaCommandLine) state;
        JavaParameters javaParameters = javaCommandLine.getJavaParameters();
        // Making the assumption that it's JVM 7 onwards
        javaParameters.getVMParametersList().addParametersString(XDEBUG);
        // Debugger port
        String remotePort = JDWP + debuggerPort;
        javaParameters.getVMParametersList().addParametersString(remotePort);
        return javaCommandLine;
    }

}
