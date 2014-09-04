package com.github.guikeller.jettyrunner.runner;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
public class JettyRunnerCommandLineTest {

    @Test
    public void testGetWebAppPath() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.when(conf.getWebappPaths()).thenReturn("/Test");
        Mockito.when(conf.getWebappFolders()).thenReturn("C:/path/to/test/webapp");

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        String webAppPath = runner.getWebAppPath();
        assertNotNull(webAppPath);
        assertEquals(" --path /Test C:\\path\\to\\test\\webapp ", webAppPath);
    }

    @Test
    public void testGetClassesDirectory() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.when(conf.getClassesDirectories()).thenReturn("C:/path/to/test/bin");

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        String classesDirectory = runner.getClassesDirectory();
        assertNotNull(classesDirectory);
        assertEquals(" --classes C:\\path\\to\\test\\bin ",classesDirectory);
    }

    @Test
    public void testGetJettyXmlPaths() {
        JettyRunnerConfiguration conf = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.when(conf.getJettyXml()).thenReturn("C:/path/to/test/xml");

        JettyRunnerCommandLine runner = Whitebox.newInstance(JettyRunnerCommandLine.class);
        runner.setModel(conf);

        String xmlPaths = runner.getJettyXmlPaths();
        assertNotNull(xmlPaths);
        assertEquals(" --config C:\\path\\to\\test\\xml ",xmlPaths);
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

}
