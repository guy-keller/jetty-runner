package com.github.guikeller.jettyrunner.ui;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.compiler.impl.ModuleCompileScope;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Controller - Jetty Runner Editor
 * @see com.intellij.openapi.options.SettingsEditor
 * @author Gui Keller
 */
public class JettyRunnerEditor extends SettingsEditor<JettyRunnerConfiguration> {

    private JettyRunnerConfPanel configurationPanel;
    private String mainOutputDirectory = "";

    public JettyRunnerEditor(JettyRunnerConfiguration jettyRunnerConfiguration) {
        this.configurationPanel = new JettyRunnerConfPanel();
        super.resetFrom(jettyRunnerConfiguration);
    }

    /**
     * This is invoked when the form is first loaded.
     * The values may be stored in disk, if not, set some defaults
     * @param jettyRunnerConfiguration jettyRunnerConfiguration
     */
    @Override
    protected void resetEditorFrom(JettyRunnerConfiguration jettyRunnerConfiguration) {
        Project project = jettyRunnerConfiguration.getProject();
        // WebApp Path
        String webappPaths = jettyRunnerConfiguration.getWebappPaths();
        if (webappPaths != null && !"".equals(webappPaths.trim())) {
            this.configurationPanel.getPathField().setText(webappPaths);
        } else {
            String projectName = project.getName();
            this.configurationPanel.getPathField().setText("/"+projectName);
        }
        // WebApp Folder (one level down to web.xml"
        String webappFolders = jettyRunnerConfiguration.getWebappFolders();
        if (webappFolders != null && !"".equals(webappFolders.trim())) {
            this.configurationPanel.getWebappField().setText(webappFolders);
        } else {
            String webAppsFolder = getWebAppsFolder(project);
            this.configurationPanel.getWebappField().setText(webAppsFolder);
        }
        // Classes directory
        String classesDirectories = jettyRunnerConfiguration.getClassesDirectories();
        if (classesDirectories != null && !"".equals(classesDirectories.trim())) {
            this.configurationPanel.getClassesField().setText(classesDirectories);
        } else {
            String outputDirectory = getMainOutputDirectory(project, jettyRunnerConfiguration.getModules());
            this.configurationPanel.getClassesField().setText(outputDirectory);
        }
        // Runs on port
        String runningOnPort = jettyRunnerConfiguration.getRunningOnPort();
        if (runningOnPort != null && !"".equals(runningOnPort)) {
            this.configurationPanel.getRunOnPortField().setText(runningOnPort);
        } else {
            this.configurationPanel.getRunOnPortField().setText("8080");
        }

        // Choose modules (all modules default)
        String selectedModuleName = jettyRunnerConfiguration.getSelectedModuleName();
        final Module[] modules = ModuleManager.getInstance(project).getModules();
        final String[] modulesNames = new String[modules.length + 1];
        modulesNames[0] = "<all modules>";
        int indexToSelect = 0;
        for (int i = 0; i < modules.length; i++) {
            final String currentModuleName = modules[i].getName();
            modulesNames[i + 1] = currentModuleName;

            if (currentModuleName.equals(selectedModuleName)) {
                indexToSelect = i + 1;
            }
        }
        this.configurationPanel.getModuleComboBox().setModel(new DefaultComboBoxModel<>(modulesNames));
        this.configurationPanel.getModuleComboBox().setSelectedIndex(indexToSelect);

        // Jetty XML (Optional)
        this.configurationPanel.getXmlField().setText(jettyRunnerConfiguration.getJettyXml());
        // Jetty Runner Path
        this.configurationPanel.getJettyRunnerField().setText(jettyRunnerConfiguration.getJettyRunnerJarPath());

        // Env Vars (Optional)
        Map<String, String> environmentVariables = jettyRunnerConfiguration.getEnvironmentVariables();
        if(environmentVariables != null && !environmentVariables.isEmpty()){
            this.configurationPanel.getEnvironmentVariables().setEnvs(environmentVariables);
        }
        // Vm Args (Optional)
        this.configurationPanel.getVmArgsField().setText(jettyRunnerConfiguration.getVmArgs());
    }

    /**
     * This is invoked when the user fills the form and pushes apply/ok
     * @param jettyRunnerConfiguration jettyRunnerConfiguration
     * @throws ConfigurationException ex
     */
    @Override
    protected void applyEditorTo(JettyRunnerConfiguration jettyRunnerConfiguration) throws ConfigurationException {
        JComboBox<String> moduleComboBox = this.configurationPanel.getModuleComboBox();
        jettyRunnerConfiguration.setSelectedModuleName(moduleComboBox.getItemAt(moduleComboBox.getSelectedIndex()));
        jettyRunnerConfiguration.setWebappPaths(this.configurationPanel.getPathField().getText());
        jettyRunnerConfiguration.setWebappFolders(this.configurationPanel.getWebappField().getText());
        jettyRunnerConfiguration.setClassesDirectories(this.configurationPanel.getClassesField().getText());
        jettyRunnerConfiguration.setRunningOnPort(this.configurationPanel.getRunOnPortField().getText());
        jettyRunnerConfiguration.setJettyXml(this.configurationPanel.getXmlField().getText());
        jettyRunnerConfiguration.setJettyRunnerJarPath(this.configurationPanel.getJettyRunnerField().getText());
        jettyRunnerConfiguration.setVmArgs(this.configurationPanel.getVmArgsField().getText());
        jettyRunnerConfiguration.setPassParentEnvironmentVariables(this.configurationPanel.getEnvironmentVariables().isPassParentEnvs());
        // Deals with adding / removing env vars before saving to the conf file
        Map<String, String> envVars = this.configurationPanel.getEnvironmentVariables().getEnvs();
        addOrRemoveEnvVar(jettyRunnerConfiguration.getEnvironmentVariables(), envVars);
        jettyRunnerConfiguration.setEnvironmentVariables(envVars);
        try {
            // Not entirely sure if 'I have to' do this - the IntelliJ framework may do
            jettyRunnerConfiguration.writeExternal(new Element(JettyRunnerConfiguration.PREFIX + UUID.randomUUID().toString()));
        } catch (WriteExternalException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return this.configurationPanel.getMainPanel();
    }

    // Helpers

    /**
     * Retrieves the output directory for the main module
     * @param project Project
     * @param modules Concerned modules array
     * @return String value
     */
    private String getMainOutputDirectory(final Project project, Module[] modules) {
        // Preparing things up for a sneaky "CompileTask"
        final CompilerManager compilerManager = CompilerManager.getInstance(project);
        final ModuleCompileScope compileScope = new ModuleCompileScope(project, modules, false);
        final Module mainModule = modules[0];
        // Though a "CompileTask" I can get hold of the "CompileContext"
        CompileTask compileTask = compileContext -> {
            // Through the "CompileContext" I can get the output directory of the main module
            VirtualFile mainOutputDirectory = compileContext.getModuleOutputDirectory(mainModule);
            if(mainOutputDirectory != null) {
                JettyRunnerEditor.this.mainOutputDirectory = mainOutputDirectory.getPresentableUrl();
            } else {
                // Project hasn't been compiled yet, so there is no output directory
                NotificationGroupManager notificationGroupManager = NotificationGroupManager.getInstance();
                NotificationGroup notificationGroup = notificationGroupManager.getNotificationGroup("IDEA_JETTY_RUNNER");
                Notification notification = notificationGroup.createNotification("Jetty Runner - Couldn't determine the classes folder:<br>Please compile / make your project before creating the conf.", NotificationType.ERROR);
                notification.notify(project);
            }
            return true;
        };
        // Executes the task (synchronously), which invokes that internal 'execute' method
        compilerManager.executeTask(compileTask, compileScope, "JettyRunner-By-GuiKeller", null);
        return this.mainOutputDirectory;
    }

    /**
     * Returns the most probable WebApps folder
     * @param project Project
     * @return String value
     */
    private String getWebAppsFolder(Project project) {
        // Using the api to look for the web.xml
        String webappsFolder = "";
        PsiShortNamesCache namesCache = PsiShortNamesCache.getInstance(project);
        PsiFile[] webXML = namesCache.getFilesByName("web.xml");
        if (webXML != null && webXML.length > 0) {
            // Grab the first one that the api found
            PsiFile file = webXML[0];
            // The parent folder is the "WEB-INF" folder
            PsiDirectory webInfFolder = file.getParent();
            if (webInfFolder != null) {
                // The parent folder to "WEB-INF" is the WebApps folder
                PsiDirectory webappFolder = webInfFolder.getParent();
                if (webappFolder != null){
                    VirtualFile virtualFile = webappFolder.getVirtualFile();
                    webappsFolder = virtualFile.getPresentableUrl();
                }
            }
        }
        return webappsFolder;
    }

    /**
     * Adds / removes variables to the System Environment Variables
     * @param currentVars Map<String,String>
     * @param newVars Map<String,String>
     */
    private void addOrRemoveEnvVar(Map<String, String> currentVars, Map<String, String> newVars){
        // Removes the current env vars
        if(currentVars !=null && !currentVars.isEmpty()) {
            Set<String> keys = currentVars.keySet();
            for (String key : keys) {
                System.clearProperty(key);
            }
        }
        // Adds the new env vars
        if(newVars != null && !newVars.isEmpty()){
            Set<String> keys = newVars.keySet();
            for(String key : keys) {
                String value = newVars.get(key);
                System.setProperty(key, value);
            }
        }
    }

    public void setConfigurationPanel(JettyRunnerConfPanel configurationPanel) {
        this.configurationPanel = configurationPanel;
    }
}
