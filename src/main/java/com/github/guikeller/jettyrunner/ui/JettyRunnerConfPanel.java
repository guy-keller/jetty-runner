package com.github.guikeller.jettyrunner.ui;

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.ui.components.fields.ExpandableTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;

/**
 * View / Presentation - Created using the WYSIWYG editor.
 * Used the JGoodies Form Layout - which is BSD.
 * @author Gui Keller
 */
public class JettyRunnerConfPanel {

    private JPanel mainPanel;
    private JTextField pathField;
    private JTextField webappField;
    private JTextField classesField;
    private JTextField runOnPortField;
    private JTextField xmlField;
    private JButton browseButton;
    private ExpandableTextField vmArgsField;
    private EnvironmentVariablesComponent environmentVariables;
    private JLabel spacerLabel;
    private JLabel vmArgsLabel;
    private JLabel xmlLabel;
    private JLabel runOnPortLabel;
    private JLabel classesLabel;
    private JLabel webappLabel;
    private JLabel pathLabel;
    private JLabel secondMsgLabel;
    private JLabel envVarLabel;
    private JLabel moduleLabel;
    private JComboBox<String> moduleComboBox;
    private JTextField jettyRunnerField;
    private JLabel jettyRunnerLabel;
    private JButton jettyRunnerButton;
    private JButton mavenRepoButton;
    private JLabel repoLabel;
    private JLabel jettyRunnerSpacer;
    private JPanel formPanel;

    public JettyRunnerConfPanel() {
        browseButton.addActionListener(e -> {
            browseJettyXml();
        });
        repoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                goToMvnRepoOnBrowser();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                repoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                repoLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        jettyRunnerButton.addActionListener((evt) -> {
            browseJettyRunnerJar();
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JTextField getJettyRunnerField() {
        return jettyRunnerField;
    }

    public JTextField getPathField() {
        return pathField;
    }

    public JTextField getWebappField() {
        return webappField;
    }

    public JTextField getClassesField() {
        return classesField;
    }

    public JTextField getRunOnPortField() {
        return runOnPortField;
    }

    public JTextField getXmlField() {
        return xmlField;
    }

    public ExpandableTextField getVmArgsField() {
        return vmArgsField;
    }

    public EnvironmentVariablesComponent getEnvironmentVariables() {
        return environmentVariables;
    }

    public JComboBox<String> getModuleComboBox() {
        return moduleComboBox;
    }

    protected void browseJettyXml() {
        // Shows a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(Boolean.TRUE);
        // Checking whether the user clicked okay
        int result = fileChooser.showOpenDialog(new JFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            StringBuffer paths = new StringBuffer();
            File[] selectedFiles = fileChooser.getSelectedFiles();
            if (selectedFiles != null && selectedFiles.length > 0) {
                for (File selectedFile : selectedFiles) {
                    // Selected files in CSV format
                    paths.append(selectedFile.getAbsolutePath() + ",");
                }
                // Removing the comma at the end
                String value = paths.substring(0, (paths.length() - 1));
                xmlField.setText(value);
            }
        }
    }

    protected void browseJettyRunnerJar() {
        // Shows a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(Boolean.FALSE);
        // Checking whether the user clicked okay
        int result = fileChooser.showOpenDialog(new JFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            StringBuffer paths = new StringBuffer();
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                // Removing the comma at the end
                String value = selectedFile.getAbsolutePath();
                jettyRunnerField.setText(value);
            }
        }
    }

    protected void goToMvnRepoOnBrowser() {
        try {
            // Opens a browser (or new tab) and goes to the url below
            String mvnRepo = "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/";
            URI uri = new URI(mvnRepo);
            Desktop.getDesktop().browse(uri);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
