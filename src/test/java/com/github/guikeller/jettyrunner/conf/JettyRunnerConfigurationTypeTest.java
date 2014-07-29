package com.github.guikeller.jettyrunner.conf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ImageIcon.class})
public class JettyRunnerConfigurationTypeTest {

    @Test
    public void testGetDisplayName(){
        JettyRunnerConfigurationType type = Whitebox.newInstance(JettyRunnerConfigurationType.class);
        String displayName = type.getDisplayName();
        assertEquals("Jetty Runner",displayName);
    }

    @Test
    public void testGetConfigurationTypeDescription(){
        JettyRunnerConfigurationType type = Whitebox.newInstance(JettyRunnerConfigurationType.class);
        String desc = type.getConfigurationTypeDescription();
        assertEquals("IntelliJ IDEA Jetty Runner",desc);
    }

    @Test
    public void testGetId(){
        JettyRunnerConfigurationType type = Whitebox.newInstance(JettyRunnerConfigurationType.class);
        String id = type.getId();
        assertEquals("JettyRunner-By-GuiKeller",id);
    }

    @Test
    public void testGetIcon(){
        PowerMockito.suppress(PowerMockito.constructorsDeclaredIn(ImageIcon.class));
        JettyRunnerConfigurationType type = Whitebox.newInstance(JettyRunnerConfigurationType.class);
        assertNotNull(type.getIcon());
    }

}