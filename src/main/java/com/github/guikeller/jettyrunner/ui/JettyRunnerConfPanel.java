package com.github.guikeller.jettyrunner.ui;

import com.intellij.execution.configuration.EnvironmentVariablesComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

/**
 * View / Presentation - Created using the WYSIWYG editor.
 * Used the JGoodies Form Layout - which is BSD.
 * @author Gui Keller
 */
public class JettyRunnerConfPanel {

    private static final String DONATION_URL = "https://www.googledrive.com/host/0Bxt4cirHQpFvYTFoZUJPbEtNWmc";

    private JPanel mainPanel;
    private JTextField pathField;
    private JTextField webappField;
    private JTextField classesField;
    private JTextField runOnPortField;
    private JTextField xmlField;
    private JButton browseButton;
    private JTextField vmArgsField;
    private EnvironmentVariablesComponent environmentVariables;
    private JLabel spacerLabel;
    private JLabel vmArgsLabel;
    private JLabel firstMsgLabel;
    private JLabel xmlLabel;
    private JLabel runOnPortLabel;
    private JLabel classesLabel;
    private JLabel webappLabel;
    private JLabel pathLabel;
    private JLabel secondMsgLabel;
    private JLabel donationLabel;
    private JLabel envVarLabel;

    public JettyRunnerConfPanel() {
        // Action executed when clicked on browse
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        });
        // Action executed when clicked on Make a Donation
        donationLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        donationLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URL(DONATION_URL).toURI());
                }catch(Exception ex){
                    throw new RuntimeException("Please try accessing: "+DONATION_URL, ex);
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
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

    public JTextField getVmArgsField() {
        return vmArgsField;
    }

    public EnvironmentVariablesComponent getEnvironmentVariables() {
        return environmentVariables;
    }

}
