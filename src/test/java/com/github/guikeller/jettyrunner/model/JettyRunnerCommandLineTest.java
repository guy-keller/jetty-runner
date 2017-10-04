package com.github.guikeller.jettyrunner.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class JettyRunnerCommandLineTest {

    @Test
    public void testGetWebAppPath() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.when(conf.getWebappPaths()).thenReturn("/Test");
        String folder = "C:/path/to/test/webapp";
        Mockito.when(conf.getWebappFolders()).thenReturn(folder);

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        String webAppPath = runner.getWebAppPath();
        assertNotNull(webAppPath);
        assertEquals(" --path /Test " + folder.replace('/', File.separatorChar) + " ", webAppPath);
    }

    @Test
    public void testGetClassesDirectory() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        String folder = "C:/path/to/test/bin";
        Mockito.when(conf.getClassesDirectories()).thenReturn(folder);

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        String classesDirectory = runner.getClassesDirectory();
        assertNotNull(classesDirectory);
        assertEquals(" --classes " + folder.replace('/', File.separatorChar) + " ",classesDirectory);
    }

    @Test
    public void testGetJettyXmlPaths() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        String folder = "C:/path/to/test/xml";
        Mockito.when(conf.getJettyXml()).thenReturn(folder);

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        String xmlPaths = runner.getJettyXmlPaths();
        assertNotNull(xmlPaths);
        assertEquals(" --config " + folder.replace('/', File.separatorChar) + " ",xmlPaths);
    }

    @Test
    public void testGetPort() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.when(conf.getRunningOnPort()).thenReturn("8080");

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        String port = runner.getPort();
        assertNotNull(port);
        assertEquals(" --port 8080 ",port);
    }

    @Test
    public void testGetVmArgs() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.when(conf.getVmArgs()).thenReturn("-Xms256m");

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        String vmArgs = runner.getVmArgs();
        assertNotNull(vmArgs);
        assertEquals("-Xms256m",vmArgs);
    }

    @Test
    public void testGetEnvVars() {
        Map<String, String> envVar = new HashMap<>(0);
        envVar.put("KEY", "VALUE");

        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.when(conf.getEnvironmentVariables()).thenReturn(envVar);

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        Map<String, String> envVars = runner.getEnvVars();
        assertNotNull(envVars);
        assertEquals(1, envVars.size());
        assertEquals("KEY", envVars.keySet().iterator().next());
        assertEquals("VALUE", envVars.values().iterator().next());
    }

    @Test
    public void testIsPassParentEnvVars() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.when(conf.isPassParentEnvironmentVariables()).thenReturn(true, false);

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        boolean value1 = runner.isPassParentEnvironmentVariables();
        assertTrue(value1);

        boolean value2 = runner.isPassParentEnvironmentVariables();
        assertFalse(value2);
    }

}
