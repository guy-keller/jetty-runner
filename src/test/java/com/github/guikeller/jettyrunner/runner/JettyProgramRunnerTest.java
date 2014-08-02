package com.github.guikeller.jettyrunner.runner;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.execution.configurations.RunProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class JettyProgramRunnerTest {

    @Test
    public void testGetRunnerId() throws Exception {
        JettyProgramDebugger debugger = Whitebox.newInstance(JettyProgramDebugger.class);
        String runnerId = debugger.getRunnerId();
        assertEquals("JettyRunner-By-GuiKeller", runnerId);
    }

    @Test
    public void testCanRun() throws Exception {
        RunProfile profile = Mockito.mock(JettyRunnerConfiguration.class);

        JettyProgramRunner runner = Whitebox.newInstance(JettyProgramRunner.class);
        boolean canRun = runner.canRun("Run", profile);
        assertTrue(canRun);

        boolean cannotRun = runner.canRun("Debug", profile);
        assertFalse(cannotRun);

        RunProfile wrongProfile = Mockito.mock(RunProfile.class);
        boolean cannotRun2 = runner.canRun("Run", wrongProfile);
        assertFalse(cannotRun2);
    }
}