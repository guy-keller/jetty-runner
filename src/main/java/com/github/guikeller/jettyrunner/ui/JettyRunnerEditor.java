package com.github.guikeller.jettyrunner.ui;

import com.github.guikeller.jettyrunner.model.JettyRunnerConfiguration;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.UUID;

/**
 * Created by Gui on 12/07/2014.
 */
public class JettyRunnerEditor extends SettingsEditor<JettyRunnerConfiguration> {

    private JTextField webappPathsField = new JTextField(20);
    private JTextField webappFoldersField = new JTextField(20);
    private JTextField classesDirectoriesField = new JTextField(20);

    private JTextField runningOnPortField = new JTextField(20);
    private JTextField jettyXmlField = new JTextField(20);

    public JettyRunnerEditor(JettyRunnerConfiguration jettyRunnerConfiguration){
        super();
        super.resetFrom(jettyRunnerConfiguration);
    }

    @Override
    protected void resetEditorFrom(JettyRunnerConfiguration jettyRunnerConfiguration) {
        this.webappPathsField.setText(jettyRunnerConfiguration.getWebappPaths());
        this.webappFoldersField.setText(jettyRunnerConfiguration.getWebappFolders());
        this.classesDirectoriesField.setText(jettyRunnerConfiguration.getClassesDirectories());
        this.runningOnPortField.setText(jettyRunnerConfiguration.getRunningOnPort());
        this.jettyXmlField.setText(jettyRunnerConfiguration.getJettyXml());
    }

    @Override
    protected void applyEditorTo(JettyRunnerConfiguration jettyRunnerConfiguration) throws ConfigurationException {
        jettyRunnerConfiguration.setWebappPaths(this.webappPathsField.getText());
        jettyRunnerConfiguration.setWebappFolders(this.webappFoldersField.getText());
        jettyRunnerConfiguration.setClassesDirectories(this.classesDirectoriesField.getText());
        jettyRunnerConfiguration.setRunningOnPort(this.runningOnPortField.getText());
        jettyRunnerConfiguration.setJettyXml(this.jettyXmlField.getText());
        try {
            // Not entirely sure if 'I have' to do this - the framework could do
            jettyRunnerConfiguration.writeExternal(new Element(JettyRunnerConfiguration.PREFIX+UUID.randomUUID().toString()));
        } catch (WriteExternalException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    protected JComponent createEditor() {

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));

        JLabel howToLabel = new JLabel();
        howToLabel.setText("The fields below may accept multiple values (comma separated).");
        controlsPanel.add(howToLabel);

        JLabel noteLabel = new JLabel();
        noteLabel.setText("*Note: Hover over the fields and buttons below for tips.");
        controlsPanel.add(noteLabel);

        JLabel spacing = new JLabel();
        spacing.setText(" ");
        controlsPanel.add(spacing);

        JPanel pathPanel = new JPanel();
        pathPanel.add(new JLabel("Path: "));
        pathPanel.add(this.webappPathsField);
        this.webappPathsField.setToolTipText("Eg: /MyWebApp1 (Accepts multiple values)");
        controlsPanel.add(pathPanel);

        JPanel foldersPanel = new JPanel();
        foldersPanel.add(new JLabel("WebApp Folder: "));
        foldersPanel.add(this.webappFoldersField);
        this.webappFoldersField.setToolTipText("Eg: /src/main/webapp (Accepts multiple values)");
        controlsPanel.add(foldersPanel);

        JPanel classesPanel = new JPanel();
        classesPanel.add(new JLabel("Classes Folder: "));
        classesPanel.add(this.classesDirectoriesField);
        this.classesDirectoriesField.setToolTipText("Eg: /target/classes (Accepts multiple values)");
        controlsPanel.add(classesPanel);

        JPanel portPanel = new JPanel();
        portPanel.add(new JLabel("Run on Port: "));
        portPanel.add(this.runningOnPortField);
        this.runningOnPortField.setToolTipText("Eg: 8080 (single value)");
        controlsPanel.add(portPanel);

        JPanel jettyPanel = new JPanel();
        jettyPanel.add(new JLabel("Jetty Xml: "));
        jettyPanel.add(this.jettyXmlField);
        this.jettyXmlField.setToolTipText("Eg: /src/main/resources/jetty.xml (Accepts multiple values) [Optional]");
        controlsPanel.add(jettyPanel);

        JButton browseToXmlsButton = new JButton("Browse XML");
        browseToXmlsButton.setToolTipText("You may select multiple files [Optional]");
        browseToXmlsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(Boolean.TRUE);
                int result = fileChooser.showOpenDialog(new JFrame());
                if(result == JFileChooser.APPROVE_OPTION) {
                    String paths = new String();
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    if(selectedFiles != null && selectedFiles.length > 0) {
                        for (File selectedFile : selectedFiles) {
                            paths += selectedFile.getAbsolutePath() + ",";
                        }
                        paths = paths.substring(0, (paths.length() - 1));
                    }
                    jettyXmlField.setText(paths);
                }
            }
        });
        controlsPanel.add(browseToXmlsButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("Jetty Runner - Run Jetty Run"));
        mainPanel.add(controlsPanel);

        return mainPanel;
    }
}
