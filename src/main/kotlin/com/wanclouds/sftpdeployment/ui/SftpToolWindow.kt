package com.wanclouds.sftpdeployment.ui

import com.wanclouds.sftpdeployment.settings.SftpSettings
import com.wanclouds.sftpdeployment.sftp.SftpClient
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.CollectionListModel
import com.intellij.ui.components.JBList
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.io.File
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel

class SftpToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = SftpChangesPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}

class SftpChangesPanel(private val project: Project) : JPanel(BorderLayout()) {
    private val listModel = CollectionListModel<File>()
    private val fileList = JBList(listModel)
    private val refreshBtn = JButton("Refresh Git Changes")
    private val uploadBtn = JButton("Upload Selected Files")

    init {
        fileList.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

        val topPanel = JPanel()
        topPanel.add(refreshBtn)
        topPanel.add(uploadBtn)

        add(topPanel, BorderLayout.NORTH)
        add(JScrollPane(fileList), BorderLayout.CENTER)

        refreshBtn.addActionListener { loadGitChanges() }
        uploadBtn.addActionListener { uploadSelectedFiles() }

        loadGitChanges()
    }

    private fun loadGitChanges() {
        val changeListManager = ChangeListManager.getInstance(project)
        val changes = changeListManager.allChanges
        val modifiedFiles = mutableListOf<File>()

        for (change in changes) {
            val vf = change.virtualFile
            if (vf != null && !vf.isDirectory) {
                modifiedFiles.add(File(vf.path))
            }
        }

        listModel.replaceAll(modifiedFiles)
    }

    private fun uploadSelectedFiles() {
        val selectedFiles = fileList.selectedValuesList
        if (selectedFiles.isEmpty()) {
            Messages.showWarningDialog(project, "Please select at least one modified file from the list.", "No Files Selected")
            return
        }

        val settings = SftpSettings.getInstance()
        val profile = settings.getActiveProfile() ?: run {
            Messages.showErrorDialog(project, "No active deployment profile configured.", "Upload Failed")
            return
        }
        val server = settings.getServerForProfile(profile) ?: run {
            Messages.showErrorDialog(project, "No SSH server associated with active profile.", "Upload Failed")
            return
        }

        val serverDisplayName = if (server.name.isNotBlank()) server.name else server.host

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Bulk Uploading to $serverDisplayName...", false) {
            override fun run(indicator: ProgressIndicator) {
                val basePath = project.basePath ?: ""
                var successCount = 0

                for ((index, localFile) in selectedFiles.withIndex()) {
                    indicator.fraction = index.toDouble() / selectedFiles.size
                    indicator.text = "Uploading (${index + 1}/${selectedFiles.size}): ${localFile.name}"

                    try {
                        val relativePath = if (localFile.path.startsWith(basePath)) {
                            localFile.path.removePrefix(basePath).removePrefix("/")
                        } else {
                            localFile.name
                        }
                        SftpClient.upload(server, localFile, profile.remotePath, relativePath)
                        successCount++
                    } catch (e: Exception) {
                        // Continue next file
                    }
                }

                ApplicationManager.getApplication().invokeLater {
                    Messages.showInfoMessage(
                        project,
                        "Successfully uploaded $successCount of ${selectedFiles.size} files to $serverDisplayName!",
                        "Bulk Upload Complete"
                    )
                }
            }
        })
    }
}
