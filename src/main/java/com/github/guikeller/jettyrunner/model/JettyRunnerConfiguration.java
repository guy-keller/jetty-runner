package com.github.guikeller.jettyrunner.model;

import com.github.guikeller.jettyrunner.runner.JettyRunnerCommandLine;
import com.github.guikeller.jettyrunner.ui.JettyRunnerEditor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Jetty Runner Configuration - Model
 * @see com.intellij.execution.configurations.LocatableConfigurationBase
 * @author Gui Keller
 */
public class JettyRunnerConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {

    public static final String PREFIX = "JettyRunnerV05-";

    private static final String WEBAPP_PATHS = PREFIX+"webappPaths";
    private static final String WEBAPP_FOLDERS = PREFIX+"webappFolders";
    private static final String CLASSES_DIRS = PREFIX+"classesDirectories";
    private static final String RUNNING_PORT = PREFIX+"runningOnPort";
    private static final String JETTY_XML = PREFIX+"jettyXml";
    private static final String VM_ARGS = PREFIX+"vmArgs";

    private String webappPaths;
    private String webappFolders;
    private String classesDirectories;

    private String runningOnPort;
    private String jettyXml;
    private String vmArgs;


    public JettyRunnerConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    public SettingsEditor<JettyRunnerConfiguration> getConfigurationEditor() {
        return new JettyRunnerEditor(this);
    }

    @Nullable
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new JettyRunnerCommandLine(executionEnvironment, this);
    }

    // Persistence of values in disk

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        // Reads the values from the disk
        Project project = super.getProject();
        PropertiesComponent storedValues = PropertiesComponent.getInstance(project);
        this.webappPaths = storedValues.getValue(WEBAPP_PATHS);
        this.webappFolders = storedValues.getValue(WEBAPP_FOLDERS);
        this.classesDirectories = storedValues.getValue(CLASSES_DIRS);
        this.runningOnPort = storedValues.getValue(RUNNING_PORT);
        this.jettyXml = storedValues.getValue(JETTY_XML);
        this.vmArgs = storedValues.getValue(VM_ARGS);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        // Persists the values in disk
        Project project = super.getProject();
        PropertiesComponent storedValues = PropertiesComponent.getInstance(project);
        storedValues.setValue(WEBAPP_PATHS, this.webappPaths);
        storedValues.setValue(WEBAPP_FOLDERS, this.webappFolders);
        storedValues.setValue(CLASSES_DIRS, this.classesDirectories);
        storedValues.setValue(RUNNING_PORT, this.runningOnPort);
        storedValues.setValue(JETTY_XML, this.jettyXml);
        storedValues.setValue(VM_ARGS, this.vmArgs);
    }

    public JettyRunnerConfiguration clone(){
        try {
            super.clone();
            // Duplication of a configuration
            Element element = new Element(PREFIX+UUID.randomUUID().toString());
            // Write the values to the new element using the current values
            this.writeExternal(element);
            // Creates a new running configuration
            Project project = super.getProject();
            ConfigurationFactory factory = super.getFactory();
            RunConfiguration template = factory.createTemplateConfiguration(project);
            // Copies the values by reading the previous conf and returns the new configuration
            JettyRunnerConfiguration configuration = (JettyRunnerConfiguration) template;
            configuration.setName(super.getName());
            configuration.readExternal(element);
            return configuration;
        } catch (Exception e) {
            // I have no idea why this would happen
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Module[] getModules() {
        return new Module[0];
    }

    // Getters and Setters

    public String getWebappPaths() {
        return webappPaths;
    }

    public void setWebappPaths(String webappPaths) {
        this.webappPaths = webappPaths;
    }

    public String getWebappFolders() {
        return webappFolders;
    }

    public void setWebappFolders(String webappFolders) {
        this.webappFolders = webappFolders;
    }

    public String getClassesDirectories() {
        return classesDirectories;
    }

    public void setClassesDirectories(String classesDirectories) {
        this.classesDirectories = classesDirectories;
    }

    public String getRunningOnPort() {
        return runningOnPort;
    }

    public void setRunningOnPort(String runningOnPort) {
        this.runningOnPort = runningOnPort;
    }

    public String getJettyXml() {
        return jettyXml;
    }

    public void setJettyXml(String jettyXml) {
        this.jettyXml = jettyXml;
    }

    public String getVmArgs() {
        return vmArgs;
    }

    public void setVmArgs(String vmArgs) {
        this.vmArgs = vmArgs;
    }

}
