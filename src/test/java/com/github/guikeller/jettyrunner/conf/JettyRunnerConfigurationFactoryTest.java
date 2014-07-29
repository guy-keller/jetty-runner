package com.github.guikeller.jettyrunner.conf;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.util.IconUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JettyRunnerConfiguration.class, IconUtil.class})
public class JettyRunnerConfigurationFactoryTest {

    @Test
    public void testCreateTemplateConfiguration() throws Exception {
        PowerMockito.mockStatic(IconUtil.class);
        PowerMockito.suppress(PowerMockito.constructorsDeclaredIn(JettyRunnerConfiguration.class));
        JettyRunnerConfigurationFactory factory = Whitebox.newInstance(JettyRunnerConfigurationFactory.class);

        Project project = Mockito.mock(Project.class);
        RunConfiguration configuration = factory.createTemplateConfiguration(project);
        assertNotNull(configuration);
    }
}
