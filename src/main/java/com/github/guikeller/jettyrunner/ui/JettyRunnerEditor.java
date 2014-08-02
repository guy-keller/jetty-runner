package com.github.guikeller.jettyrunner.ui;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.compiler.impl.ModuleCompileScope;
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
import java.util.UUID;

/**
 * Controller - Jetty Runner Editor
 * @see com.intellij.openapi.options.SettingsEditor
 * @author Gui Keller
 */
public class JettyRunnerEditor extends SettingsEditor<JettyRunnerConfiguration> {

    private JettyRunnerConfPanel configurationPanel;
    private String mainOutputDirectory;

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
        if (jettyRunnerConfiguration.getWebappPaths() != null) {
            this.configurationPanel.getPathField().setText(jettyRunnerConfiguration.getWebappPaths());
        } else {
            String projectName = project.getName();
            this.configurationPanel.getPathField().setText("/"+projectName);
        }
        // WebApp Folder (one level down to web.xml"
        if (jettyRunnerConfiguration.getWebappFolders() != null) {
            this.configurationPanel.getWebappField().setText(jettyRunnerConfiguration.getWebappFolders());
        } else {
            String webAppsFolder = getWebAppsFolder(project);
            this.configurationPanel.getWebappField().setText(webAppsFolder);
        }
        // Classes directory
        if (jettyRunnerConfiguration.getClassesDirectories() != null) {
            this.configurationPanel.getClassesField().setText(jettyRunnerConfiguration.getClassesDirectories());
        } else {
            String outputDirectory = getMainOutputDirectory(project);
            this.configurationPanel.getClassesField().setText(outputDirectory);
        }
        // Runs on port
        if (jettyRunnerConfiguration.getRunningOnPort() != null) {
            this.configurationPanel.getRunOnPortField().setText(jettyRunnerConfiguration.getRunningOnPort());
        } else {
            this.configurationPanel.getRunOnPortField().setText("8080");
        }
        // Jetty XML (Optional)
        this.configurationPanel.getXmlField().setText(jettyRunnerConfiguration.getJettyXml());
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
        jettyRunnerConfiguration.setJettyXml(this.configurationPanel.getXmlField().getText());
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
    private String getMainOutputDirectory(Project project) {
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
                JettyRunnerEditor.this.mainOutputDirectory = mainOutputDirectory.getPresentableUrl();
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

    public void setConfigurationPanel(JettyRunnerConfPanel configurationPanel) {
        this.configurationPanel = configurationPanel;
    }
}
