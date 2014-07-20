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
import org.mortbay.jetty.runner.Runner;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Gui on 12/07/2014.
 */
public class JettyRunnerCommandLine extends JavaCommandLineState {

    // TODO - Make the address (port) configurable
    private static final String JDWP = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5007";

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

        // Dynamic variables
        String basePath = project.getBasePath();
        javaParams.setWorkingDirectory(basePath);

        String path = this.getWebAppPath(basePath);
        javaParams.getProgramParametersList().addParametersString(path);

        String classes = this.getClassesDirectory(basePath);
        javaParams.getProgramParametersList().addParametersString(classes);

        String jettyXmls = this.getJettyXmlPaths(basePath);
        if(jettyXmls != null) {
            javaParams.getProgramParametersList().addParametersString(jettyXmls);
        }

        String port = this.getPort();
        javaParams.getProgramParametersList().addParametersString(port);

        // Unfortunately life is short, and docs on writing a plugin even shorter
        javaParams.getVMParametersList().addParametersString("-Xdebug");
        javaParams.getVMParametersList().addParametersString(JDWP);

        return javaParams;
    }

    private String getWebAppPath(String basePath) {
        String paths = model.getWebappPaths();
        String folders = model.getWebappFolders();

        String[] pathsArray = null;
        if(paths != null && !paths.isEmpty()){
            pathsArray = paths.split(",");
        }

        String[] foldersArray = null;
        if(folders != null && !folders.isEmpty()) {
            foldersArray = folders.split(",");
        }

        if( pathsArray != null && pathsArray.length > 0
                && foldersArray != null && foldersArray.length >0 ){

            if(foldersArray.length < pathsArray.length){
                throw new IllegalArgumentException("Incorrect folder(s) param: "+folders);
            }

            String value = " --path ";
            for(int i=0; i<pathsArray.length; i++){
                value += pathsArray[i] + " ";
                String fullPath = basePath+foldersArray[i];
                value += PathUtil.toPresentableUrl(fullPath);
            }
            return value;
        }

        throw new IllegalArgumentException("Invalid Path(s) or Folder(s): "+paths+" / "+folders);
    }

    private String getClassesDirectory(String basePath) {
        String classes = model.getClassesDirectories();

        String[] classArray = null;
        if(classes != null && !classes.isEmpty()){
            classArray = classes.split(",");
        }

        if(classArray != null && classArray.length > 0){
            String value = " --classes ";
            for (String clazz : classArray){
                value += basePath+clazz;
            }
            return PathUtil.toPresentableUrl(value);
        }
        throw new IllegalArgumentException("Invalid classes folder: "+classes);
    }

    private String getJettyXmlPaths(String basePath) {
        String xmls = model.getJettyXml();

        String[] xmlArray = null;
        if(xmls != null && !xmls.isEmpty()){
            xmlArray = xmls.split(",");
        }

        if(xmlArray != null && xmlArray.length > 0){
            String value = " --config ";
            for (String jettyXml : xmlArray){
                value += basePath+jettyXml;
            }
            return PathUtil.toPresentableUrl(value);
        }
        return null;
    }

    private String getPort() {
        String runningOnPort = model.getRunningOnPort();
        if(runningOnPort != null && !runningOnPort.isEmpty()){
            return " --port "+runningOnPort;
        }
        throw new IllegalArgumentException("Invalid port: "+runningOnPort);
    }

}
