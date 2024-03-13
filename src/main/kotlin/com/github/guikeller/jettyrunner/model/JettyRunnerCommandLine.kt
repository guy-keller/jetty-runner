package com.github.guikeller.jettyrunner.model

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.JavaCommandLineState
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.project.Project
import com.intellij.util.PathUtil

/**
 * Jetty Runner Command Line - Runner Model - Holds the vars to Start / Execute Jetty
 * Visit - http://www.eclipse.org/jetty/documentation/current/runner.html
 * @see com.intellij.execution.configurations.JavaCommandLineState
 *
 * @author Guy Keller
 */
class JettyRunnerCommandLine(environment: ExecutionEnvironment, model: JettyRunnerConfiguration) : JavaCommandLineState(environment) {

    private var environment: ExecutionEnvironment = environment
    private var model: JettyRunnerConfiguration

    companion object {
        // The jetty-runner main class
        private const val JETTY_MAIN_CLASS = "org.eclipse.jetty.runner.Runner"
    }

    init {
        this.model = model
    }

    @Throws(ExecutionException::class)
    override fun createJavaParameters(): JavaParameters {
        val javaParams: JavaParameters = JavaParameters()
        javaParams.mainClass = JETTY_MAIN_CLASS
        // Use the same JDK as the project
        val project: Project = environment.project
        val manager: ProjectRootManager = ProjectRootManager.getInstance(project)
        javaParams.jdk = manager.projectSdk

        // All modules to use the same things
        val modules = model.modules
        if (modules.isNotEmpty()) {
            for (module in modules) {
                javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES)
            }
        }

        // Dynamically adds the 'jetty-runner.jar' to the classpath
        val jettyRunnerjarPath = jettyRunnerJarPath
        javaParams.classPath.add(jettyRunnerjarPath)

        // Jetty XML - configured by the user
        val jettyXmls = this.jettyXmlPaths
        if (jettyXmls != null) {
            javaParams.programParametersList.addParametersString(jettyXmls)
        }
        // Port - configured by the user (default 8080)
        val port = this.port
        javaParams.programParametersList.addParametersString(port)
        // Dynamic variables - working directory
        val basePath = project.basePath
        javaParams.workingDirectory = basePath
        // Path - configured by the user
        val path = this.webAppPath
        javaParams.programParametersList.addParametersString(path)
        // Classes - configured by the user
        val classes = this.classesDirectory
        javaParams.programParametersList.addParametersString(classes)
        // VM Args
        val vmArgs = this.vmArgs
        if (vmArgs != null) {
            javaParams.vmParametersList.addParametersString(vmArgs)
        }
        // Env Vars
        val environmentVariables = this.envVars
        if (!environmentVariables.isNullOrEmpty()) {
            // Pass it through to the VM that will be created when running jetty-runner
            val keys = environmentVariables.keys
            for (key in keys) {
                val value = environmentVariables[key]
                javaParams.vmParametersList.addProperty(key, value)
            }
        }
        // All done, run it
        return javaParams
    }

    // - - - Helpers - - -

    /**
     * Retrieves the "path" parameter
     * Jetty expects: /Path1 /path/to/WebApps
     * @return String value
     */
    private val webAppPath: String
    get() {
        val value = StringBuffer(" --path ")
        val paths = model.webappPaths
        val folders = model.webappFolders
        // Multiple values allowed - CSV
        var pathsArray = arrayOf<String?>()
        if (!paths.isNullOrEmpty()) {
            pathsArray = paths.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        // Multiple values allowed - CSV
        var foldersArray = arrayOf<String?>()
        if (!folders.isNullOrEmpty()) {
            foldersArray = folders.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        // Checking that we have paths and folders
        if (pathsArray.isNotEmpty() && foldersArray.isNotEmpty()) {
            // Creates the 'path' parameter
            for (i in pathsArray.indices) {
                val path = pathsArray[i]
                val folderPath = PathUtil.toPresentableUrl(foldersArray[i]!!)
                value.append(path).append(" ").append(folderPath).append(" ")
            }
        } else {
            throw IllegalArgumentException("Number of Path(s) and Folder(s) must match: $paths / $folders")
        }
        return value.toString()
    }

    /**
     * Retrieves the "classes" parameter
     * @return String
     */
    private val classesDirectory: String
    get() {
        val classesDirectories = model.classesDirectories
        // Multiple values allowed - CSV
        var classesFolders: Array<String?>? = null
        if (!classesDirectories.isNullOrEmpty()) {
            classesFolders = classesDirectories.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        // Creates the 'classes' parameter
        if (!classesFolders.isNullOrEmpty()) {
            val value = StringBuffer(" --classes ")
            for (classesFolder in classesFolders) {
                value.append(PathUtil.toPresentableUrl(classesFolder!!)).append(" ")
            }
            return value.toString()
        }
        throw IllegalArgumentException("Invalid classes folder: $classesDirectories")
    }

    /**
     * Retrieves the "config" parameter.
     * Using the specified jetty XML files
     * @return String
     */
    private val jettyXmlPaths: String?
    get() {
        val xmls = model.jettyXml
        // Multiple values allowed - CSV
        var xmlArray: Array<String?>? = null
        if (!xmls.isNullOrEmpty()) {
            xmlArray = xmls.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        // Creates the 'config' parameter
        if (!xmlArray.isNullOrEmpty()) {
            val value = StringBuffer(" --config ")
            for (jettyXml in xmlArray) {
                value.append(PathUtil.toPresentableUrl(jettyXml!!)).append(" ")
            }
            return value.toString()
        }
        return null
    }

    /**
     * Retrieves the "port" parameter
     * @return String
     */
    private val port: String
    get() {
        val runningOnPort = model.runningOnPort
        //Single value - not optional
        if (!runningOnPort.isNullOrEmpty()) {
            return " --port $runningOnPort "
        }
        throw IllegalArgumentException("Invalid port: $runningOnPort")
    }

    /**
     * Retrieves the "VM Args" parameter
     * @return String
     */
    private val vmArgs: String?
    get() {
        val vmArgs = model.vmArgs
        return if (!vmArgs.isNullOrEmpty()) vmArgs else null
    }

    /**
     * Retrieves the Env Vars
     * @return Map<String></String>, String>
     */
    private val envVars: Map<String, String>?
    get() {
        val environmentVariables = model.environmentVariables
        if (!environmentVariables.isNullOrEmpty()) {
            return model.environmentVariables
        }
        return HashMap(0)
    }

    private val jettyRunnerJarPath: String?
        get() {
            val jettyRunnerJarPath = model.jettyRunnerJarPath
            if (jettyRunnerJarPath != null && jettyRunnerJarPath.trim { it <= ' ' }.isNotEmpty()) {
                return model.jettyRunnerJarPath
            }
            throw IllegalArgumentException("Invalid Jetty Runner Path: $jettyRunnerJarPath")
        }

    /**
     * Returns whether to pass through system / ide environment variables
     * @return boolean
     */
    val isPassParentEnvironmentVariables: Boolean
    get() = model.isPassParentEnvironmentVariables

    fun setModel(model: JettyRunnerConfiguration) {
        this.model = model
    }

}
