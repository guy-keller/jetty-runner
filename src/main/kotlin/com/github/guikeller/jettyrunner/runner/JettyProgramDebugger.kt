package com.github.guikeller.jettyrunner.runner

import com.intellij.debugger.engine.DebuggerUtils
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration
import com.intellij.debugger.impl.GenericDebuggerRunner
import com.intellij.execution.configurations.*
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.RunContentDescriptor


/**
 * Jetty Program Debugger - Jetty Runner on Debug mode
 * @see com.intellij.debugger.impl.GenericDebuggerRunner
 *
 * @author Guy Keller
 */
class JettyProgramDebugger : GenericDebuggerRunner() {

    override fun canRun(executorId: String, runProfile: RunProfile): Boolean {
        // It can only run JettyRunnerConfigurations
        return executorId == DefaultDebugExecutor.EXECUTOR_ID && runProfile is JettyRunnerConfiguration
    }

    @Throws(ExecutionException::class)
    override fun createContentDescriptor(state: RunProfileState,
                                          environment: ExecutionEnvironment): RunContentDescriptor? {
        // Now we figure out if is the Debug button has been hit
        val executor: Executor = environment.executor
        // If was the debug, then we do some extra magic
        if (executor is DefaultDebugExecutor) {
            val debuggerPort: String = DebuggerUtils.getInstance().findAvailableDebugAddress(true)
            // Get hold of the JavaParameters
            val stateWithDebug: RunProfileState = addParamsToJavaCmdLine(state, debuggerPort)
            // Creating a 'Remote' configuration on the fly
            val connection: RemoteConnection = RemoteConnection(true, LOCALHOST, debuggerPort, false)
            // Attaches the remote configuration to the VM and then starts it up
            return super.attachVirtualMachine(stateWithDebug, environment, connection, true)
        } else {
            // If it was something else then we don't do anything special
            return super.createContentDescriptor(state, environment)
        }
    }

    @Throws(ExecutionException::class)
    private fun addParamsToJavaCmdLine(state: RunProfileState, debuggerPort: String): RunProfileState {
        val javaCommandLine: JavaCommandLine = state as JavaCommandLine
        val javaParameters: JavaParameters = javaCommandLine.javaParameters
        // Making the assumption that it's JVM 7 onwards
        javaParameters.vmParametersList.addParametersString(XDEBUG)
        // Debugger port
        val remotePort = JDWP + debuggerPort
        javaParameters.vmParametersList.addParametersString(remotePort)
        return javaCommandLine
    }

    companion object {
        // These are JVM 8 onwards hence why the JVM 8 as min requirement
        private const val XDEBUG = "-Xdebug"
        private const val JDWP = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address="

        private const val LOCALHOST = "127.0.0.1"
    }
}
