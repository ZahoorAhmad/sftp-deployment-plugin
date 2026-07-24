package com.wanclouds.sftpdeployment.actions

import com.wanclouds.sftpdeployment.settings.SftpSettings
import com.wanclouds.sftpdeployment.sftp.SftpClient
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

abstract class BaseSftpAction : AnAction() {
    protected fun notify(project: Project?, title: String, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("SFTP Connection Test")
            .createNotification(title, content, type)
            .notify(project)
    }

    protected fun getSelectedFiles(e: AnActionEvent): List<VirtualFile> {
        val filesArray = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (!filesArray.isNullOrEmpty()) {
            return filesArray.toList()
        }
        val singleFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        return if (singleFile != null) listOf(singleFile) else emptyList()
    }
}

class UploadAction : BaseSftpAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFiles = getSelectedFiles(e)
        if (virtualFiles.isEmpty()) {
            notify(project, "Upload Failed", "No files selected.", NotificationType.WARNING)
            return
        }
        uploadVirtualFiles(project, virtualFiles)
    }

    companion object {
        fun uploadVirtualFiles(project: Project, virtualFiles: List<VirtualFile>) {
            val settings = SftpSettings.getInstance()
            val profile = settings.getActiveProfile() ?: run {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("SFTP Connection Test")
                    .createNotification("Upload Failed", "No active deployment profile selected.", NotificationType.WARNING)
                    .notify(project)
                return
            }
            val server = settings.getServerForProfile(profile) ?: run {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("SFTP Connection Test")
                    .createNotification("Upload Failed", "No SSH server associated with active profile.", NotificationType.ERROR)
                    .notify(project)
                return
            }

            val serverDisplayName = if (server.name.isNotBlank()) server.name else server.host

            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Uploading ${virtualFiles.size} file(s) to $serverDisplayName...", false) {
                override fun run(indicator: ProgressIndicator) {
                    var successCount = 0
                    val basePath = project.basePath ?: ""

                    for ((index, virtualFile) in virtualFiles.withIndex()) {
                        if (virtualFile.isDirectory) continue
                        indicator.fraction = index.toDouble() / virtualFiles.size
                        indicator.text = "Uploading (${index + 1}/${virtualFiles.size}): ${virtualFile.name}"

                        try {
                            val localFile = File(virtualFile.path)
                            val relativePath = if (virtualFile.path.startsWith(basePath)) {
                                virtualFile.path.removePrefix(basePath).removePrefix("/")
                            } else {
                                virtualFile.name
                            }

                            SftpClient.upload(server, localFile, profile.remotePath, relativePath)
                            successCount++
                        } catch (ex: Exception) {
                            // ignore individual failure
                        }
                    }

                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("SFTP Connection Test")
                        .createNotification(
                            "Upload Finished", 
                            "Successfully uploaded $successCount of ${virtualFiles.size} file(s) to $serverDisplayName (${profile.remotePath})", 
                            if (successCount > 0) NotificationType.INFORMATION else NotificationType.ERROR
                        )
                        .notify(project)
                }
            })
        }
    }
}

class UploadAllChangedAction : BaseSftpAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val changeListManager = ChangeListManager.getInstance(project)
        val changes = changeListManager.allChanges
        val changedVirtualFiles = changes.mapNotNull { it.virtualFile }.filter { !it.isDirectory }

        if (changedVirtualFiles.isEmpty()) {
            notify(project, "Upload All Changes", "No modified Git files found to upload.", NotificationType.INFORMATION)
            return
        }

        UploadAction.uploadVirtualFiles(project, changedVirtualFiles)
    }
}

class DownloadAction : BaseSftpAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFiles = getSelectedFiles(e)
        if (virtualFiles.isEmpty()) {
            notify(project, "Download Failed", "No files selected.", NotificationType.WARNING)
            return
        }

        val settings = SftpSettings.getInstance()
        val profile = settings.getActiveProfile() ?: run {
            notify(project, "Download Failed", "No active deployment profile selected.", NotificationType.WARNING)
            return
        }
        val server = settings.getServerForProfile(profile) ?: run {
            notify(project, "Download Failed", "No SSH server associated with active profile.", NotificationType.ERROR)
            return
        }

        val serverDisplayName = if (server.name.isNotBlank()) server.name else server.host

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Downloading ${virtualFiles.size} file(s) from $serverDisplayName...", false) {
            override fun run(indicator: ProgressIndicator) {
                var successCount = 0
                val basePath = project.basePath ?: ""

                for ((index, virtualFile) in virtualFiles.withIndex()) {
                    if (virtualFile.isDirectory) continue
                    indicator.fraction = index.toDouble() / virtualFiles.size
                    indicator.text = "Downloading (${index + 1}/${virtualFiles.size}): ${virtualFile.name}"

                    try {
                        val relativePath = if (virtualFile.path.startsWith(basePath)) {
                            virtualFile.path.removePrefix(basePath).removePrefix("/")
                        } else {
                            virtualFile.name
                        }
                        val remoteFilePath = "${profile.remotePath}/$relativePath"

                        SftpClient.download(server, remoteFilePath, virtualFile.path)
                        virtualFile.refresh(false, false)
                        successCount++
                    } catch (ex: Exception) {
                        // ignore individual failure
                    }
                }

                notify(
                    project, 
                    "Download Finished", 
                    "Successfully downloaded $successCount of ${virtualFiles.size} file(s) from $serverDisplayName", 
                    if (successCount > 0) NotificationType.INFORMATION else NotificationType.ERROR
                )
            }
        })
    }
}

class SyncAction : BaseSftpAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        notify(project, "SFTP Sync", "Project sync triggered.", NotificationType.INFORMATION)
    }
}
