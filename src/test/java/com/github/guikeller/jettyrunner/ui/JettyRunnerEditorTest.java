package com.github.guikeller.jettyrunner.ui;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import org.jdom.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.swing.*;

import static org.junit.Assert.fail;

@RunWith(PowerMockRunner.class)
public class JettyRunnerEditorTest {

    @Test
    public void testApplyEditorTo() throws Exception {
        // Set up
        JettyRunnerConfiguration configuration = Mockito.mock(JettyRunnerConfiguration.class);
        Mockito.doThrow(new IllegalArgumentException("MY-TEST")).when(configuration).writeExternal(Mockito.any(Element.class));

        JettyRunnerConfPanel confPanel = Mockito.mock(JettyRunnerConfPanel.class);

        JTextField pathField = Mockito.mock(JTextField.class);
        Mockito.when(confPanel.getPathField()).thenReturn(pathField);

        JTextField webappField = Mockito.mock(JTextField.class);
        Mockito.when(confPanel.getWebappField()).thenReturn(webappField);

        JTextField classesField = Mockito.mock(JTextField.class);
        Mockito.when(confPanel.getClassesField()).thenReturn(classesField);

        JTextField runOnPortField = Mockito.mock(JTextField.class);
        Mockito.when(confPanel.getRunOnPortField()).thenReturn(runOnPortField);

        JTextField xmlField = Mockito.mock(JTextField.class);
        Mockito.when(confPanel.getXmlField()).thenReturn(xmlField);

        JTextField vmArgsField = Mockito.mock(JTextField.class);
        Mockito.when(confPanel.getVmArgsField()).thenReturn(vmArgsField);

        JettyRunnerEditor editor = Whitebox.newInstance(JettyRunnerEditor.class);
        editor.setConfigurationPanel(confPanel);

        // Test
        try {
            editor.applyEditorTo(configuration);
        }catch(Exception ex){
            if(!ex.getMessage().contains("MY-TEST")){
                ex.printStackTrace();
                fail(ex.getMessage());
            }
        }

        Mockito.verify(confPanel, Mockito.times(1)).getClassesField();
        Mockito.verify(confPanel, Mockito.times(1)).getPathField();
        Mockito.verify(confPanel, Mockito.times(1)).getWebappField();
        Mockito.verify(confPanel, Mockito.times(1)).getRunOnPortField();
        Mockito.verify(confPanel, Mockito.times(1)).getXmlField();
        Mockito.verify(confPanel, Mockito.times(1)).getVmArgsField();

        Mockito.verify(classesField, Mockito.times(1)).getText();
        Mockito.verify(pathField, Mockito.times(1)).getText();
        Mockito.verify(webappField, Mockito.times(1)).getText();
        Mockito.verify(runOnPortField, Mockito.times(1)).getText();
        Mockito.verify(xmlField, Mockito.times(1)).getText();
        Mockito.verify(vmArgsField, Mockito.times(1)).getText();

    }
}