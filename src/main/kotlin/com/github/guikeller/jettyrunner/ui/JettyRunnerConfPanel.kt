package com.github.guikeller.jettyrunner.ui

import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.ui.components.fields.ExpandableTextField
import java.awt.Cursor
import java.awt.Desktop
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*
import kotlin.Exception
import kotlin.RuntimeException
import kotlin.String

/**
 * View / Presentation - Created using the WYSIWYG editor.
 * Used the JGoodies Form Layout - which is BSD.
 * @author Guy Keller
 */
class JettyRunnerConfPanel {

    var mainPanel: JPanel? = null
    @JvmField
    var pathField: JTextField? = null
    @JvmField
    var webappField: JTextField? = null
    @JvmField
    var classesField: JTextField? = null
    @JvmField
    var runOnPortField: JTextField? = null
    @JvmField
    var xmlField: JTextField? = null
    private var browseButton: JButton? = null
    @JvmField
    var vmArgsField: ExpandableTextField? = null
    @JvmField
    var environmentVariables: EnvironmentVariablesComponent? = null

    private var spacerLabel: JLabel? = null
    private var vmArgsLabel: JLabel? = null
    private var xmlLabel: JLabel? = null
    private var runOnPortLabel: JLabel? = null
    private var classesLabel: JLabel? = null
    private var webappLabel: JLabel? = null
    private var pathLabel: JLabel? = null
    private var secondMsgLabel: JLabel? = null
    private var envVarLabel: JLabel? = null
    private var moduleLabel: JLabel? = null

    @JvmField
    var moduleComboBox: JComboBox<String>? = null
    var jettyRunnerField: JTextField? = null
    private var jettyRunnerLabel: JLabel? = null
    private var jettyRunnerButton: JButton? = null
    private var mavenRepoButton: JButton? = null
    private var repoLabel: JLabel? = null
    private var jettyRunnerSpacer: JLabel? = null
    private var formPanel: JPanel? = null

    init {
        browseButton!!.addActionListener { _: ActionEvent? ->
            browseJettyXml()
        }
        repoLabel!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                goToMvnRepoOnBrowser()
            }

            override fun mouseEntered(e: MouseEvent) {
                repoLabel!!.cursor = Cursor(Cursor.HAND_CURSOR)
            }

            override fun mouseExited(e: MouseEvent) {
                repoLabel!!.cursor = Cursor(Cursor.DEFAULT_CURSOR)
            }
        })
        jettyRunnerButton!!.addActionListener { _: ActionEvent? ->
            browseJettyRunnerJar()
        }
    }

    protected fun browseJettyXml() {
        // Shows a file chooser
        val fileChooser = JFileChooser()
        fileChooser.isMultiSelectionEnabled = true
        // Checking whether the user clicked okay
        val result = fileChooser.showOpenDialog(JFrame())
        if (result == JFileChooser.APPROVE_OPTION) {
            val paths = StringBuffer()
            val selectedFiles = fileChooser.selectedFiles
            if (selectedFiles != null && selectedFiles.size > 0) {
                for (selectedFile in selectedFiles) {
                    // Selected files in CSV format
                    paths.append(selectedFile.absolutePath + ",")
                }
                // Removing the comma at the end
                val value = paths.substring(0, (paths.length - 1))
                xmlField!!.text = value
            }
        }
    }

    private fun browseJettyRunnerJar() {
        // Shows a file chooser
        val fileChooser = JFileChooser()
        fileChooser.isMultiSelectionEnabled = false
        // Checking whether the user clicked okay
        val result = fileChooser.showOpenDialog(JFrame())
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            if (selectedFile != null) {
                // Removing the comma at the end
                val value = selectedFile.absolutePath
                jettyRunnerField!!.text = value
            }
        }
    }

    private fun goToMvnRepoOnBrowser() {
        try {
            // Opens a browser (or new tab) and goes to the url below
            val mvnRepo = "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/"
            val uri = URI(mvnRepo)
            Desktop.getDesktop().browse(uri)
        } catch (ex: Exception) {
            throw RuntimeException(ex.message, ex)
        }
    }
}
