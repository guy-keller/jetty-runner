package com.github.guikeller.jettyrunner.runner;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.mortbay.jetty.runner.Runner;

/**
 * Jetty Runner Command Line - Starts / Executes Jetty
 * Visit - http://www.eclipse.org/jetty/documentation/current/runner.html
 * @see com.intellij.execution.configurations.JavaCommandLineState
 * @author Gui Keller
 */
public class JettyRunnerCommandLine extends JavaCommandLineState {

    // Jetty "Main Class" - the target
    private static final String MAIN_CLASS = Runner.class.getName();

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
        // Use the same JDK as the project
        Project project = this.environment.getProject();
        ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());
        // All modules to use the same things
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules != null && modules.length > 0) {
            for (Module module : modules) {
                javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES_AND_TESTS);
            }
        }
        // Dynamically adds the jetty-runner.jar to the classpath
        String jarPath = PathUtil.getJarPathForClass(Runner.class);
        javaParams.getClassPath().add(jarPath);
        javaParams.setMainClass(MAIN_CLASS);
        // Jetty XML - configured by the user
        String jettyXmls = this.getJettyXmlPaths();
        if(jettyXmls != null) {
            javaParams.getProgramParametersList().addParametersString(jettyXmls);
        }
        // Dynamic variables - working directory
        String basePath = project.getBasePath();
        javaParams.setWorkingDirectory(basePath);
        // Path - configured by the user
        String path = this.getWebAppPath();
        javaParams.getProgramParametersList().addParametersString(path);
        // Classes - configured by the user
        String classes = this.getClassesDirectory();
        javaParams.getProgramParametersList().addParametersString(classes);
        // Port - configured by the user (default 8080)
        String port = this.getPort();
        javaParams.getProgramParametersList().addParametersString(port);
        // VM Args
        String vmArgs = this.getVmArgs();
        if(vmArgs != null) {
            javaParams.getVMParametersList().addParametersString(vmArgs);
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
        String paths = model.getWebappPaths();
        String folders = model.getWebappFolders();
        // Multiple values allowed - CSV
        String[] pathsArray = null;
        if(paths != null && !paths.isEmpty()){
            pathsArray = paths.split(",");
        }
        // Multiple values allowed - CSV
        String[] foldersArray = null;
        if(folders != null && !folders.isEmpty()) {
            foldersArray = folders.split(",");
        }
        // Checking that we have paths and folders
        if( pathsArray != null && pathsArray.length > 0
                && foldersArray != null && foldersArray.length >0 ){
            // One path, one folder - two paths, two folders..
            if(foldersArray.length != pathsArray.length){
                throw new IllegalArgumentException("Incorrect folder(s) param: "+folders);
            }
            // Creates the 'path' parameter
            StringBuffer value = new StringBuffer(" --path ");
            for(int i=0; i<pathsArray.length; i++){
                String path = pathsArray[i];
                String folderPath = PathUtil.toPresentableUrl(foldersArray[i]);
                value.append(path).append(" ").append(folderPath).append(" ");
            }
            return value.toString();
        }
        throw new IllegalArgumentException("Number of Path(s) and Folder(s) must math: "+paths+" / "+folders);
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

    protected String getVmArgs() {
        String vmArgs = model.getVmArgs();
        return vmArgs != null && !vmArgs.isEmpty() ? vmArgs : null;
    }

    public void setModel(JettyRunnerConfiguration model) {
        this.model = model;
    }

}
