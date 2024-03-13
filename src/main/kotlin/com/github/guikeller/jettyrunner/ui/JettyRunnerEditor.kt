package com.github.guikeller.jettyrunner.ui

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration
import com.intellij.compiler.impl.ModuleCompileScope
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.compiler.CompileTask
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.search.PsiShortNamesCache
import org.jdom.Element
import java.util.*
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JComponent

/**
 * Controller - Jetty Runner Editor
 * @see com.intellij.openapi.options.SettingsEditor
 *
 * @author Guy Keller
 */
class JettyRunnerEditor(jettyRunnerConfiguration: JettyRunnerConfiguration?) : SettingsEditor<JettyRunnerConfiguration>() {

    private var configurationPanel: JettyRunnerConfPanel
    private var mainOutputDirectory = ""

    init {
        this.configurationPanel = JettyRunnerConfPanel()
        super.resetFrom(jettyRunnerConfiguration)
    }

    /**
     * This is invoked when the form is first loaded.
     * The values may be stored in disk, if not, set some defaults
     * @param jettyRunnerConfiguration jettyRunnerConfiguration
     */
    override fun resetEditorFrom(jettyRunnerConfiguration: JettyRunnerConfiguration) {
        val project: Project = jettyRunnerConfiguration.project
        // WebApp Path
        val webappPaths: String = jettyRunnerConfiguration.webappPaths.toString()
        if ("" != webappPaths.trim { it <= ' ' }) {
            configurationPanel.pathField?.text = webappPaths
        } else {
            val projectName = project.name
            configurationPanel.pathField?.text = "/$projectName"
        }
        // WebApp Folder (one level down to web.xml"
        val webappFolders: String = jettyRunnerConfiguration.webappFolders.toString()
        if ("" != webappFolders.trim { it <= ' ' }) {
            configurationPanel.webappField?.text = webappFolders
        } else {
            val webAppsFolder = getWebAppsFolder(project)
            configurationPanel.webappField?.text = webAppsFolder
        }
        // Classes directory
        val classesDirectories: String = jettyRunnerConfiguration.classesDirectories.toString()
        if ("" != classesDirectories.trim { it <= ' ' }) {
            configurationPanel.classesField?.text = classesDirectories
        } else {
            val outputDirectory = getMainOutputDirectory(project, jettyRunnerConfiguration.getModules())
            configurationPanel.classesField?.text = outputDirectory
        }
        // Runs on port
        val runningOnPort: String = jettyRunnerConfiguration.runningOnPort.toString()
        if ("" != runningOnPort) {
            configurationPanel.runOnPortField?.text = runningOnPort
        } else {
            configurationPanel.runOnPortField?.text = "8080"
        }

        // Choose modules (all modules default)
        val selectedModuleName: String = jettyRunnerConfiguration.selectedModuleName.toString()
        val modules: Array<Module> = ModuleManager.getInstance(project).modules
        val modulesNames = arrayOfNulls<String>(modules.size + 1)
        modulesNames[0] = "<all modules>"
        var indexToSelect = 0
        for (i in modules.indices) {
            val currentModuleName = modules[i].name
            modulesNames[i + 1] = currentModuleName

            if (currentModuleName == selectedModuleName) {
                indexToSelect = i + 1
            }
        }
        configurationPanel.moduleComboBox?.setModel(DefaultComboBoxModel<String>(modulesNames))
        configurationPanel.moduleComboBox?.selectedIndex = indexToSelect

        // Jetty XML (Optional)
        configurationPanel.xmlField?.text = jettyRunnerConfiguration.jettyXml
        // Jetty Runner Path
        configurationPanel.jettyRunnerField?.text = jettyRunnerConfiguration.jettyRunnerJarPath

        // Env Vars (Optional)
        val environmentVariables: Map<String, String>? = jettyRunnerConfiguration.environmentVariables
        if (!environmentVariables.isNullOrEmpty()) {
            configurationPanel.environmentVariables?.envs = environmentVariables
        }
        // Vm Args (Optional)
        configurationPanel.vmArgsField?.text = jettyRunnerConfiguration.vmArgs
    }

    /**
     * This is invoked when the user fills the form and pushes apply/ok
     * @param jettyRunnerConfiguration jettyRunnerConfiguration
     * @throws ConfigurationException ex
     */
    @Throws(ConfigurationException::class)
    public override fun applyEditorTo(jettyRunnerConfiguration: JettyRunnerConfiguration) {
        val moduleComboBox: JComboBox<String>? = configurationPanel.moduleComboBox
        jettyRunnerConfiguration.selectedModuleName = moduleComboBox?.getItemAt(moduleComboBox.getSelectedIndex())
        jettyRunnerConfiguration.webappPaths = configurationPanel.pathField?.text
        jettyRunnerConfiguration.webappFolders = configurationPanel.webappField?.text
        jettyRunnerConfiguration.classesDirectories = configurationPanel.classesField?.text
        jettyRunnerConfiguration.runningOnPort = configurationPanel.runOnPortField?.text
        jettyRunnerConfiguration.jettyXml = configurationPanel.xmlField?.text
        jettyRunnerConfiguration.jettyRunnerJarPath = configurationPanel.jettyRunnerField?.text
        jettyRunnerConfiguration.vmArgs = configurationPanel.vmArgsField?.text
        jettyRunnerConfiguration.isPassParentEnvironmentVariables = configurationPanel.environmentVariables?.isPassParentEnvs!!
        // Deals with adding / removing env vars before saving to the conf file
        val envVars = configurationPanel.environmentVariables?.envs
        addOrRemoveEnvVar(jettyRunnerConfiguration.environmentVariables, envVars)
        jettyRunnerConfiguration.environmentVariables = envVars
        try {
            // Not entirely sure if 'I have to' do this - the IntelliJ framework may do
            jettyRunnerConfiguration.writeExternal(Element(JettyRunnerConfiguration.PREFIX + UUID.randomUUID().toString()))
        } catch (e: WriteExternalException) {
            throw RuntimeException(e)
        }
    }

    override fun createEditor(): JComponent {
        return configurationPanel.mainPanel!!
    }

    // Helpers
    /**
     * Retrieves the output directory for the main module
     * @param project Project
     * @param modules Concerned modules array
     * @return String value
     */
    private fun getMainOutputDirectory(project: Project, modules: Array<Module>): String {
        // Preparing things up for a sneaky "CompileTask"
        val compilerManager: CompilerManager = CompilerManager.getInstance(project)
        val compileScope: ModuleCompileScope = ModuleCompileScope(project, modules, false)
        val mainModule = modules[0]
        // Though a "CompileTask" I can get hold of the "CompileContext"
        val compileTask: CompileTask = CompileTask { compileContext ->
            // Through the "CompileContext" I can get the output directory of the main module
            val mainOutputDirectory: VirtualFile? = compileContext.getModuleOutputDirectory(mainModule)
            if (mainOutputDirectory != null) {
                this@JettyRunnerEditor.mainOutputDirectory = mainOutputDirectory.presentableUrl
            } else {
                // Project hasn't been compiled yet, so there is no output directory
                val notificationGroupManager: NotificationGroupManager = NotificationGroupManager.getInstance()
                val notificationGroup: NotificationGroup = notificationGroupManager.getNotificationGroup("JETTY-RUNNER")
                val notification: Notification = notificationGroup.createNotification("Jetty Runner - Couldn't determine the classes folder:<br>Please compile / make your project before creating the conf.", NotificationType.ERROR)
                notification.notify(project)
            }
            true
        }
        // Executes the task (synchronously), which invokes that internal 'execute' method
        compilerManager.executeTask(compileTask, compileScope, "JettyRunner-By-GuiKeller", null)
        return this.mainOutputDirectory
    }

    /**
     * Returns the most probable WebApps folder
     * @param project Project
     * @return String value
     */
    private fun getWebAppsFolder(project: Project): String {
        // Using the api to look for the web.xml
        var webappsFolder = ""
        val namesCache: PsiShortNamesCache = PsiShortNamesCache.getInstance(project)
        val webXML: Array<PsiFile> = namesCache.getFilesByName("web.xml")
        if (webXML.isNotEmpty()) {
            // Grab the first one that the api found
            val file = webXML[0]
            // The parent folder is the "WEB-INF" folder
            val webInfFolder = file.parent
            if (webInfFolder != null) {
                // The parent folder to "WEB-INF" is the WebApps folder
                val webappFolder = webInfFolder.parent
                if (webappFolder != null) {
                    val virtualFile = webappFolder.virtualFile
                    webappsFolder = virtualFile.presentableUrl
                }
            }
        }
        return webappsFolder
    }

    /**
     * Adds / removes variables to the System Environment Variables
     * @param currentVars Map<String></String>,String>
     * @param newVars Map<String></String>,String>
     */
    private fun addOrRemoveEnvVar(currentVars: Map<String, String>?, newVars: Map<String, String>?) {
        // Removes the current env vars
        if (!currentVars.isNullOrEmpty()) {
            val keys = currentVars.keys
            for (key in keys) {
                System.clearProperty(key)
            }
        }
        // Adds the new env vars
        if (!newVars.isNullOrEmpty()) {
            val keys = newVars.keys
            for (key in keys) {
                val value = newVars[key]
                if (value != null) {
                    System.setProperty(key, value)
                }
            }
        }
    }

    fun setConfigurationPanel(configurationPanel: JettyRunnerConfPanel) {
        this.configurationPanel = configurationPanel
    }
}
