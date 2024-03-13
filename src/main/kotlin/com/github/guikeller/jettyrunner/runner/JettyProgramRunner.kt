package com.github.guikeller.jettyrunner.runner

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.DefaultJavaProgramRunner

/**
 * Jetty Program Runner - Default / Run
 * @see com.intellij.execution.impl.DefaultJavaProgramRunner
 *
 * @author Guy Keller
 */
class JettyProgramRunner : DefaultJavaProgramRunner() {

    override fun canRun(executorId: String, runProfile: RunProfile): Boolean {
        // It can only run JettyRunnerConfigurations
        return executorId == DefaultRunExecutor.EXECUTOR_ID && runProfile is JettyRunnerConfiguration
    }
}
