package com.github.guikeller.jettyrunner.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

    public JettyRunnerConfPanel() {
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(Boolean.TRUE);
                int result = fileChooser.showOpenDialog(new JFrame());
                if (result == JFileChooser.APPROVE_OPTION) {
                    String paths = new String();
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    if (selectedFiles != null && selectedFiles.length > 0) {
                        for (File selectedFile : selectedFiles) {
                            paths += selectedFile.getAbsolutePath() + ",";
                        }
                        paths = paths.substring(0, (paths.length() - 1));
                    }
                    xmlField.setText(paths);
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

}
