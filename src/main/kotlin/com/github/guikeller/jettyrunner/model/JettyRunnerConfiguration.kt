package com.github.guikeller.jettyrunner.model

import com.github.guikeller.jettyrunner.ui.JettyRunnerEditor
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunProfileWithCompileBeforeLaunchOption
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element
import java.util.*

/**
 * Jetty Runner Configuration - UI Model
 * @see com.intellij.execution.configurations.LocatableConfigurationBase
 *
 * @author Guy Keller
 */
class JettyRunnerConfiguration(private val project: Project, factory: ConfigurationFactory?, name: String?) : LocatableConfigurationBase<Any?>(project, factory!!, name), RunProfileWithCompileBeforeLaunchOption {

    companion object {
        const val PREFIX: String = "JettyRunnerV130-"
        const val SELECTED_MODULE_NAME_FIELD: String = PREFIX + "SelectedModuleName"
        const val WEBAPP_PATH_FIELD: String = PREFIX + "WebAppPath"
        const val WEBAPP_FOLDER_FIELD: String = PREFIX + "WebAppFolder"
        const val CLASSES_DIRECTORY_FIELD: String = PREFIX + "ClassesDirectory"
        const val RUN_PORT_FIELD: String = PREFIX + "RunOnPort"
        const val JETTY_XML_FIELD: String = PREFIX + "JettyXML"
        const val JETTY_RUNNER_JAR_PATH: String = PREFIX + "JettyRunnerPath"
        const val VM_ARGS_FIELD: String = PREFIX + "VmArgs"
        const val PASS_PARENT_ENV_VARS_FIELD: String = PREFIX + "PassParentEnvVars"
    }

    // Getters and Setters
    @JvmField
    var webappPaths: String? = ""
    @JvmField
    var webappFolders: String? = ""
    @JvmField
    var classesDirectories: String? = ""

    @JvmField
    var runningOnPort: String? = ""
    @JvmField
    var jettyRunnerJarPath: String? = ""
    @JvmField
    var jettyXml: String? = ""
    @JvmField
    var vmArgs: String? = ""

    @JvmField
    var environmentVariables: Map<String, String>? = HashMap(0)
    var isPassParentEnvironmentVariables: Boolean = false

    @JvmField
    var selectedModuleName: String? = ""


    override fun getConfigurationEditor(): SettingsEditor<JettyRunnerConfiguration> {
        // Instantiates a new UI (Conf Window)
        return JettyRunnerEditor(this)
    }

    @Throws(ExecutionException::class)
    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState {
        // Runner Model
        return JettyRunnerCommandLine(executionEnvironment, this)
    }

    // Persistence of values in disk
    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        super.readExternal(element)
        // Reads the conf file into this class
        this.webappPaths = JDOMExternalizerUtil.readField(element, WEBAPP_PATH_FIELD)
        this.selectedModuleName = JDOMExternalizerUtil.readField(element, SELECTED_MODULE_NAME_FIELD)
        this.webappFolders = JDOMExternalizerUtil.readField(element, WEBAPP_FOLDER_FIELD)
        this.classesDirectories = JDOMExternalizerUtil.readField(element, CLASSES_DIRECTORY_FIELD)
        this.runningOnPort = JDOMExternalizerUtil.readField(element, RUN_PORT_FIELD)
        this.jettyXml = JDOMExternalizerUtil.readField(element, JETTY_XML_FIELD)
        this.jettyRunnerJarPath = JDOMExternalizerUtil.readField(element, JETTY_RUNNER_JAR_PATH)
        this.vmArgs = JDOMExternalizerUtil.readField(element, VM_ARGS_FIELD)
        val passParentEnvironmentVariablesValue = JDOMExternalizerUtil.readField(element, PASS_PARENT_ENV_VARS_FIELD)
        this.isPassParentEnvironmentVariables = passParentEnvironmentVariablesValue.toBoolean()
        EnvironmentVariablesComponent.readExternal(element, this.environmentVariables)
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        // Stores the values of this class into the parent
        JDOMExternalizerUtil.writeField(element, SELECTED_MODULE_NAME_FIELD, this.selectedModuleName)
        JDOMExternalizerUtil.writeField(element, WEBAPP_PATH_FIELD, this.webappPaths)
        JDOMExternalizerUtil.writeField(element, WEBAPP_FOLDER_FIELD, this.webappFolders)
        JDOMExternalizerUtil.writeField(element, CLASSES_DIRECTORY_FIELD, this.classesDirectories)
        JDOMExternalizerUtil.writeField(element, RUN_PORT_FIELD, this.runningOnPort)
        JDOMExternalizerUtil.writeField(element, JETTY_RUNNER_JAR_PATH, this.jettyRunnerJarPath)
        JDOMExternalizerUtil.writeField(element, JETTY_XML_FIELD, this.jettyXml)
        JDOMExternalizerUtil.writeField(element, VM_ARGS_FIELD, this.vmArgs)
        JDOMExternalizerUtil.writeField(element, PASS_PARENT_ENV_VARS_FIELD, isPassParentEnvironmentVariables.toString())
        if (this.environmentVariables != null && !environmentVariables!!.isEmpty()) {
            EnvironmentVariablesComponent.writeExternal(element, environmentVariables!!)
        }
    }

    override fun getModules(): Array<Module> {
        val modules = ModuleManager.getInstance(this.project).modules
        if (this.selectedModuleName == null) {
            return modules
        } else {
            val selectedModule = Arrays.stream(modules).filter { module: Module -> selectedModuleName == module.name }.findFirst()
            if (modules.isNotEmpty()) {
                // if we found a selected module, we use it, otherwise, we add all modules
                if (selectedModule.isPresent) {
                    return arrayOf(selectedModule.get())
                }
            }
            return modules
        }
    }

}
