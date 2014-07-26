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
 * Created by Gui on 12/07/2014.
 */
public class JettyRunnerEditor extends SettingsEditor<JettyRunnerConfiguration> {

    protected JettyRunnerConfigurationPanel configurationPanel;
    private String mainOutputDirectory = "";

    public JettyRunnerEditor(JettyRunnerConfiguration jettyRunnerConfiguration){
        this.configurationPanel = new JettyRunnerConfigurationPanel();
        super.resetFrom(jettyRunnerConfiguration);
    }

    @Override
    protected void resetEditorFrom(JettyRunnerConfiguration jettyRunnerConfiguration) {
        Project project = jettyRunnerConfiguration.getProject();
        // WebApp Path
        if(jettyRunnerConfiguration.getWebappPaths() != null && !jettyRunnerConfiguration.getWebappPaths().isEmpty()) {
            this.configurationPanel.getPathField().setText(jettyRunnerConfiguration.getWebappPaths());
        }else{
            String projectName = project.getName();
            this.configurationPanel.getPathField().setText("/"+projectName);
        }

        // WebApp Folder (one level down to web.xml"
        if(jettyRunnerConfiguration.getWebappFolders() != null && !jettyRunnerConfiguration.getWebappFolders().isEmpty()){
            this.configurationPanel.getWebappField().setText(jettyRunnerConfiguration.getWebappFolders());
        }else{
            String webAppsFolder = getWebAppsFolder(project);
            this.configurationPanel.getWebappField().setText(webAppsFolder);
        }
        // Classes directory
        if(jettyRunnerConfiguration.getClassesDirectories() != null && !jettyRunnerConfiguration.getClassesDirectories().isEmpty()){
            this.configurationPanel.getClassesField().setText(jettyRunnerConfiguration.getClassesDirectories());
        }else{
            String outputDirectory = getMainOutputDirectory(project);
            this.configurationPanel.getClassesField().setText(outputDirectory);
        }
        // Runs on port
        if(jettyRunnerConfiguration.getRunningOnPort() != null && !jettyRunnerConfiguration.getRunningOnPort().isEmpty()){
            this.configurationPanel.getRunOnPortField().setText(jettyRunnerConfiguration.getRunningOnPort());
        }else{
            this.configurationPanel.getRunOnPortField().setText("8080");
        }
        // Debugger port
        if(jettyRunnerConfiguration.getDebuggerPort() != null && !jettyRunnerConfiguration.getDebuggerPort().isEmpty()){
            this.configurationPanel.getDebuggerField().setText(jettyRunnerConfiguration.getDebuggerPort());
        }else{
            this.configurationPanel.getDebuggerField().setText("5005");
        }
        // Jetty XML (Optional)
        this.configurationPanel.getXmlField().setText(jettyRunnerConfiguration.getJettyXml());
    }

    @Override
    protected void applyEditorTo(JettyRunnerConfiguration jettyRunnerConfiguration) throws ConfigurationException {
        jettyRunnerConfiguration.setWebappPaths(this.configurationPanel.getPathField().getText());
        jettyRunnerConfiguration.setWebappFolders(this.configurationPanel.getWebappField().getText());
        jettyRunnerConfiguration.setClassesDirectories(this.configurationPanel.getClassesField().getText());
        jettyRunnerConfiguration.setRunningOnPort(this.configurationPanel.getRunOnPortField().getText());
        jettyRunnerConfiguration.setDebuggerPort(this.configurationPanel.getDebuggerField().getText());
        jettyRunnerConfiguration.setJettyXml(this.configurationPanel.getXmlField().getText());
        try {
            // Not entirely sure if 'I have' to do this - the framework could do
            jettyRunnerConfiguration.writeExternal(new Element(JettyRunnerConfiguration.PREFIX+UUID.randomUUID().toString()));
        } catch (WriteExternalException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return this.configurationPanel;
    }

    // Helpers

    private String getMainOutputDirectory(Project project){
        final CompilerManager compilerManager = CompilerManager.getInstance(project);
        final Module[] modules = ModuleManager.getInstance(project).getModules();
        final ModuleCompileScope compileScope = new ModuleCompileScope(project, modules, false);

        CompileTask compileTask = new CompileTask() {
            public boolean execute(CompileContext compileContext) {
                VirtualFile mainOutputDirectory = compileContext.getModuleOutputDirectory(modules[0]);
                if(mainOutputDirectory != null) {
                    JettyRunnerEditor.this.mainOutputDirectory = mainOutputDirectory.getPresentableUrl();
                }
                return true;
            }
        };
        compilerManager.executeTask(compileTask, compileScope, "JettyRunner", null);
        return mainOutputDirectory;
    }

    private String getWebAppsFolder(Project project){
        PsiShortNamesCache namesCache = PsiShortNamesCache.getInstance(project);
        PsiFile[] webXML = namesCache.getFilesByName("web.xml");
        if (webXML == null || webXML.length < 1) return "";

        PsiFile file = webXML[0];
        PsiDirectory webInfFolder = file.getParent();
        if(webInfFolder == null) return "";
        PsiDirectory webappFolder = webInfFolder.getParent();
        if(webappFolder == null) return "";

        VirtualFile virtualFile = webappFolder.getVirtualFile();
        return virtualFile.getPresentableUrl();
    }
}
