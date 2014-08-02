package com.github.guikeller.jettyrunner.model;

import com.github.guikeller.jettyrunner.runner.JettyRunnerCommandLine;
import com.github.guikeller.jettyrunner.ui.JettyRunnerEditor;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JettyRunnerConfiguration.class})
public class JettyRunnerConfigurationTest {

    @Test
    public void testGetConfigurationEditor() throws Exception {
        JettyRunnerEditor runEditor = Mockito.mock(JettyRunnerEditor.class);
        PowerMockito.whenNew(JettyRunnerEditor.class).withAnyArguments().thenReturn(runEditor);

        JettyRunnerConfiguration runnerConf = Whitebox.newInstance(JettyRunnerConfiguration.class);
        SettingsEditor<JettyRunnerConfiguration> editor = runnerConf.getConfigurationEditor();
        assertNotNull(editor);

        PowerMockito.verifyNew(JettyRunnerEditor.class).withArguments(runnerConf);
    }

    @Test
    public void testGetState() throws Exception {
        JettyRunnerCommandLine commandLine = Mockito.mock(JettyRunnerCommandLine.class);
        PowerMockito.whenNew(JettyRunnerCommandLine.class).withAnyArguments().thenReturn(commandLine);

        Executor executor = Mockito.mock(Executor.class);
        ExecutionEnvironment environment = Mockito.mock(ExecutionEnvironment.class);

        JettyRunnerConfiguration runnerConf = Whitebox.newInstance(JettyRunnerConfiguration.class);
        RunProfileState state = runnerConf.getState(executor, environment);
        assertNotNull(state);

        PowerMockito.verifyNew(JettyRunnerCommandLine.class).withArguments(environment, runnerConf);
    }

}