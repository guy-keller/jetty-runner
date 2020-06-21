package com.github.guikeller.jettyrunner.model;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Jetty Runner Command Line - Runner Model - Holds the vars to Start / Execute Jetty
 * Visit - http://www.eclipse.org/jetty/documentation/current/runner.html
 * @see com.intellij.execution.configurations.JavaCommandLineState
 * @author Gui Keller
 */
public class JettyRunnerCommandLine extends JavaCommandLineState {

    // The jetty-runner main class
    private static final String JETTY_MAIN_CLASS = "org.eclipse.jetty.runner.Runner";

    private ExecutionEnvironment environment;
    private JettyRunnerConfiguration model;

    public JettyRunnerCommandLine(@NotNull ExecutionEnvironment environment, JettyRunnerConfiguration model) {
        super(environment);
        this.environment = environment;
        this.model = model;
    }

    @Override
    public JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters javaParams = new JavaParameters();
        javaParams.setMainClass(JETTY_MAIN_CLASS);
        // Use the same JDK as the project
        Project project = this.environment.getProject();
        ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());

        // All modules to use the same things
        Module[] modules = this.model.getModules();
        if (modules != null && modules.length > 0) {
            for (Module module : modules) {
                javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES);
            }
        }

        // Dynamically adds the 'jetty-runner.jar' to the classpath
        String jettyRunnerjarPath = getJettyRunnerJarPath();
        javaParams.getClassPath().add(jettyRunnerjarPath);

        // Jetty XML - configured by the user
        String jettyXmls = this.getJettyXmlPaths();
        if(jettyXmls != null) {
            javaParams.getProgramParametersList().addParametersString(jettyXmls);
        }
        // Port - configured by the user (default 8080)
        String port = this.getPort();
        javaParams.getProgramParametersList().addParametersString(port);
        // Dynamic variables - working directory
        String basePath = project.getBasePath();
        javaParams.setWorkingDirectory(basePath);
        // Path - configured by the user
        String path = this.getWebAppPath();
        javaParams.getProgramParametersList().addParametersString(path);
        // Classes - configured by the user
        String classes = this.getClassesDirectory();
        javaParams.getProgramParametersList().addParametersString(classes);
        // VM Args
        String vmArgs = this.getVmArgs();
        if(vmArgs != null) {
            javaParams.getVMParametersList().addParametersString(vmArgs);
        }
        // Env Vars
        Map<String, String> environmentVariables = this.getEnvVars();
        if(!environmentVariables.isEmpty()) {
            // Pass it through to the VM that will be created when running jetty-runner
            Set<String> keys = environmentVariables.keySet();
            for(String key : keys) {
                String value = environmentVariables.get(key);
                javaParams.getVMParametersList().addProperty(key, value);
            }
        }
        // All done, run it
        return javaParams;
    }

    // Helpers

    /**
     * Retrieves the "path" parameter
     * Jetty expects: /Path1 /path/to/WebApps
     * @return String value
     */
    protected String getWebAppPath() {
        StringBuffer value = new StringBuffer(" --path ");
        String paths = model.getWebappPaths();
        String folders = model.getWebappFolders();
        // Multiple values allowed - CSV
        String[] pathsArray = new String[]{};
        if(paths != null && !paths.isEmpty()){
            pathsArray = paths.split(",");
        }
        // Multiple values allowed - CSV
        String[] foldersArray = new String[]{};
        if(folders != null && !folders.isEmpty()) {
            foldersArray = folders.split(",");
        }
        // Checking that we have paths and folders
        if(pathsArray.length > 0 && foldersArray.length > 0) {
            // Creates the 'path' parameter
            for(int i=0; i<pathsArray.length; i++){
                String path = pathsArray[i];
                String folderPath = PathUtil.toPresentableUrl(foldersArray[i]);
                value.append(path).append(" ").append(folderPath).append(" ");
            }
        } else {
            throw new IllegalArgumentException("Number of Path(s) and Folder(s) must match: " + paths + " / " + folders);
        }
        return value.toString();
    }

    /**
     * Retrieves the "classes" parameter
     * @return String
     */
    protected String getClassesDirectory() {
        String classesDirectories = model.getClassesDirectories();
        // Multiple values allowed - CSV
        String[] classesFolders = null;
        if(classesDirectories != null && !classesDirectories.isEmpty()){
            classesFolders = classesDirectories.split(",");
        }
        // Creates the 'classes' parameter
        if(classesFolders != null && classesFolders.length > 0){
            StringBuffer value = new StringBuffer(" --classes ");
            for (String classesFolder : classesFolders){
                value.append(PathUtil.toPresentableUrl(classesFolder)).append(" ");
            }
            return value.toString();
        }
        throw new IllegalArgumentException("Invalid classes folder: "+classesDirectories);
    }

    /**
     * Retrieves the "config" parameter.
     * Using the specified jetty XML files
     * @return String
     */
    protected String getJettyXmlPaths() {
        String xmls = model.getJettyXml();
        // Multiple values allowed - CSV
        String[] xmlArray = null;
        if(xmls != null && !xmls.isEmpty()){
            xmlArray = xmls.split(",");
        }
        // Creates the 'config' parameter
        if(xmlArray != null && xmlArray.length > 0){
            StringBuffer value = new StringBuffer(" --config ");
            for (String jettyXml : xmlArray){
                value.append(PathUtil.toPresentableUrl(jettyXml)).append(" ");
            }
            return value.toString();
        }
        return null;
    }

    /**
     * Retrieves the "port" parameter
     * @return String
     */
    protected String getPort() {
        String runningOnPort = model.getRunningOnPort();
        //Single value - not optional
        if(runningOnPort != null && !runningOnPort.isEmpty()){
            return " --port "+runningOnPort+" ";
        }
        throw new IllegalArgumentException("Invalid port: "+runningOnPort);
    }

    /**
     * Retrieves the "VM Args" parameter
     * @return String
     */
    protected String getVmArgs() {
        String vmArgs = model.getVmArgs();
        return vmArgs != null && !vmArgs.isEmpty() ? vmArgs : null;
    }

    /**
     * Retrieves the Env Vars
     * @return Map<String, String>
     */
    protected Map<String, String> getEnvVars() {
        Map<String, String> environmentVariables = model.getEnvironmentVariables();
        if(environmentVariables != null && !environmentVariables.isEmpty()) {
            return model.getEnvironmentVariables();
        }
        return new HashMap<String, String>(0);
    }

    protected String getJettyRunnerJarPath() {
        String jettyRunnerJarPath = model.getJettyRunnerJarPath();
        if (jettyRunnerJarPath != null && jettyRunnerJarPath.trim().length() > 0) {
            return model.getJettyRunnerJarPath();
        }
        throw new IllegalArgumentException("Invalid Jetty Runner Path: "+ jettyRunnerJarPath);
    }

    /**
     * Returns whether to pass through system / ide environment variables
     * @return boolean
     */
    protected boolean isPassParentEnvironmentVariables() {
        return model.isPassParentEnvironmentVariables();
    }

    public void setModel(JettyRunnerConfiguration model) {
        this.model = model;
    }

}
