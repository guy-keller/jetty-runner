package com.github.guikeller.jettyrunner.model;

import com.github.guikeller.jettyrunner.ui.JettyRunnerEditor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunProfileWithCompileBeforeLaunchOption;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Jetty Runner Configuration - UI Model
 * @see com.intellij.execution.configurations.LocatableConfigurationBase
 * @author Gui Keller
 */
public class JettyRunnerConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {

    public static final String PREFIX = "JettyRunnerV112-";
    public static final String WEBAPP_PATH_FIELD = PREFIX + "WebAppPath";
    public static final String WEBAPP_FOLDER_FIELD = PREFIX + "WebAppFolder";
    public static final String CLASSES_DIRECTORY_FIELD = PREFIX + "ClassesDirectory";
    public static final String RUN_PORT_FIELD = PREFIX + "RunOnPort";
    public static final String MODULE_FIELD = PREFIX + "Module";
    public static final String JETTY_XML_FIELD = PREFIX + "JettyXML";
    public static final String VM_ARGS_FIELD = PREFIX + "VmArgs";
    public static final String PASS_PARENT_ENV_VARS_FIELD = PREFIX + "PassParentEnvVars";

    private String webappPaths;
    private String webappFolders;
    private String classesDirectories;

    private String runningOnPort;
    private String module;
    private String jettyXml;
    private String vmArgs;

    private Map<String, String> environmentVariables = new HashMap<String,String>(0);
    private boolean passParentEnvironmentVariables = false;

    private Project project;


    public JettyRunnerConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
        this.project = project;
    }

    @Override
    @NotNull
    public SettingsEditor<JettyRunnerConfiguration> getConfigurationEditor() {
        // Instantiates a new UI (Conf Window)
        return new JettyRunnerEditor(this);
    }

    @Override
    @Nullable
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        // Runner Model
        return new JettyRunnerCommandLine(executionEnvironment, this);
    }

    // Persistence of values in disk

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        // Reads the conf file into this class
        this.webappPaths = JDOMExternalizerUtil.readField(element, WEBAPP_PATH_FIELD);
        this.webappFolders = JDOMExternalizerUtil.readField(element, WEBAPP_FOLDER_FIELD);
        this.classesDirectories = JDOMExternalizerUtil.readField(element, CLASSES_DIRECTORY_FIELD);
        this.runningOnPort = JDOMExternalizerUtil.readField(element, RUN_PORT_FIELD);
        this.module = JDOMExternalizerUtil.readField(element, MODULE_FIELD);
        this.jettyXml = JDOMExternalizerUtil.readField(element, JETTY_XML_FIELD);
        this.vmArgs = JDOMExternalizerUtil.readField(element, VM_ARGS_FIELD);
        String passParentEnvironmentVariablesValue = JDOMExternalizerUtil.readField(element, PASS_PARENT_ENV_VARS_FIELD);
        this.passParentEnvironmentVariables = Boolean.valueOf(passParentEnvironmentVariablesValue);
        EnvironmentVariablesComponent.readExternal(element, this.environmentVariables);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        // Stores the values of this class into the parent
        JDOMExternalizerUtil.writeField(element, WEBAPP_PATH_FIELD, this.getWebappPaths());
        JDOMExternalizerUtil.writeField(element, WEBAPP_FOLDER_FIELD, this.getWebappFolders());
        JDOMExternalizerUtil.writeField(element, CLASSES_DIRECTORY_FIELD, this.getClassesDirectories());
        JDOMExternalizerUtil.writeField(element, RUN_PORT_FIELD, this.getRunningOnPort());
        JDOMExternalizerUtil.writeField(element, MODULE_FIELD, this.getModule());
        JDOMExternalizerUtil.writeField(element, JETTY_XML_FIELD, this.getJettyXml());
        JDOMExternalizerUtil.writeField(element, VM_ARGS_FIELD, this.getVmArgs());
        JDOMExternalizerUtil.writeField(element, PASS_PARENT_ENV_VARS_FIELD, ""+this.isPassParentEnvironmentVariables());
        if(this.environmentVariables != null && !this.environmentVariables.isEmpty()){
            EnvironmentVariablesComponent.writeExternal(element, this.getEnvironmentVariables());
        }
    }

    @Override
    @NotNull
    public Module[] getModules() {
        ModuleManager moduleManager = ModuleManager.getInstance(this.project);
        return moduleManager.getModules();
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

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
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

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean isPassParentEnvironmentVariables() {
        return passParentEnvironmentVariables;
    }

    public void setPassParentEnvironmentVariables(boolean passParentEnvironmentVariables) {
        this.passParentEnvironmentVariables = passParentEnvironmentVariables;
    }

}
