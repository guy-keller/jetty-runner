package com.github.guikeller.jettyrunner.ui;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.compiler.impl.ModuleCompileScope;
import com.intellij.notification.*;
import com.intellij.openapi.compiler.CompileContext;
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
            String outputDirectory = getMainOutputDirectory(project);
            this.configurationPanel.getClassesField().setText(outputDirectory);
        }
        // Runs on port
        String runningOnPort = jettyRunnerConfiguration.getRunningOnPort();
        if (runningOnPort != null && !"".equals(runningOnPort)) {
            this.configurationPanel.getRunOnPortField().setText(runningOnPort);
        } else {
            this.configurationPanel.getRunOnPortField().setText("8080");
        }
        // Runs module
        String module = jettyRunnerConfiguration.getModule();
        if (module != null && !"".equals(module)) {
            this.configurationPanel.getModuleField().setText(module);
        } else {
            this.configurationPanel.getModuleField().setText("");
        }
        // Jetty XML (Optional)
        this.configurationPanel.getXmlField().setText(jettyRunnerConfiguration.getJettyXml());
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
        jettyRunnerConfiguration.setWebappPaths(this.configurationPanel.getPathField().getText());
        jettyRunnerConfiguration.setWebappFolders(this.configurationPanel.getWebappField().getText());
        jettyRunnerConfiguration.setClassesDirectories(this.configurationPanel.getClassesField().getText());
        jettyRunnerConfiguration.setRunningOnPort(this.configurationPanel.getRunOnPortField().getText());
        jettyRunnerConfiguration.setModule(this.configurationPanel.getModuleField().getText());
        jettyRunnerConfiguration.setJettyXml(this.configurationPanel.getXmlField().getText());
        jettyRunnerConfiguration.setVmArgs(this.configurationPanel.getVmArgsField().getText());
        jettyRunnerConfiguration.setPassParentEnvironmentVariables(this.configurationPanel.getEnvironmentVariables().isPassParentEnvs());
        // Deals with adding / removing env vars before saving to the conf file
        Map<String, String> envVars = this.configurationPanel.getEnvironmentVariables().getEnvs();
        addOrRemoveEnvVar(jettyRunnerConfiguration.getEnvironmentVariables(), envVars);
        jettyRunnerConfiguration.setEnvironmentVariables(envVars);
        try {
            // Not entirely sure if 'I have' to do this - the IntelliJ framework may do
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
     * @return String value
     */
    private String getMainOutputDirectory(final Project project) {
        // Preparing things up for a sneaky "CompileTask"
        final CompilerManager compilerManager = CompilerManager.getInstance(project);
        final Module[] modules = ModuleManager.getInstance(project).getModules();
        final ModuleCompileScope compileScope = new ModuleCompileScope(project, modules, false);
        final Module mainModule = modules[0];
        // Though a "CompileTask" I can get hold of the "CompileContext"
        CompileTask compileTask = new CompileTask() {
            public boolean execute(CompileContext compileContext) {
                // Through the "CompileContext" I can get the output directory of the main module
                VirtualFile mainOutputDirectory = compileContext.getModuleOutputDirectory(mainModule);
                if(mainOutputDirectory != null) {
                    String mainOutputDirectoryValue = mainOutputDirectory.getPresentableUrl();
                    JettyRunnerEditor.this.mainOutputDirectory = mainOutputDirectoryValue;
                } else {
                    // Project hasn't been compiled yet, so there is no output directory
                    NotificationGroup notificationGroup = new NotificationGroup("IDEA Jetty Runner", NotificationDisplayType.BALLOON, true);
                    Notification notification = notificationGroup.createNotification("Jetty Runner - Couldn't determine the classes folder:<br>Please compile / make your project before creating the conf.", NotificationType.ERROR);
                    Notifications.Bus.notify(notification, project);
                }
                return true;
            }
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
        PsiShortNamesCache namesCache = PsiShortNamesCache.getInstance(project);
        PsiFile[] webXML = namesCache.getFilesByName("web.xml");
        if (webXML == null || webXML.length < 1) return "";
        // Grab the first one that the api found
        PsiFile file = webXML[0];
        // The parent folder is the "WEB-INF" folder
        PsiDirectory webInfFolder = file.getParent();
        if (webInfFolder == null) return "";
        // The parent folder to "WEB-INF" is the WebApps folder
        PsiDirectory webappFolder = webInfFolder.getParent();
        if (webappFolder == null) return "";
        // Folder found, returns it to the user
        VirtualFile virtualFile = webappFolder.getVirtualFile();
        return virtualFile.getPresentableUrl();
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
