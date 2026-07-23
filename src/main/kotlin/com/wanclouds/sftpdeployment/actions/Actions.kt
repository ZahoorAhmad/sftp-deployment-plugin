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
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

abstract class BaseSftpAction : AnAction() {
    protected fun notify(project: Project?, title: String, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("SFTP Connection Test")
            .createNotification(title, content, type)
            .notify(project)
    }
}

class UploadAction : BaseSftpAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val settings = SftpSettings.getInstance()
        val profile = settings.getActiveProfile() ?: run {
            notify(project, "Upload Failed", "No active deployment profile selected.", NotificationType.WARNING)
            return
        }
        val server = settings.getServerForProfile(profile) ?: run {
            notify(project, "Upload Failed", "No SSH server associated with active profile.", NotificationType.ERROR)
            return
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Uploading to ${server.host}...", false) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    val basePath = project.basePath ?: ""
                    val localFile = File(virtualFile.path)
                    val relativePath = if (virtualFile.path.startsWith(basePath)) {
                        virtualFile.path.removePrefix(basePath).removePrefix("/")
                    } else {
                        virtualFile.name
                    }

                    SftpClient.upload(server, localFile, profile.remotePath, relativePath)
                    notify(project, "Upload Successful", "Uploaded ${virtualFile.name} to ${profile.remotePath}", NotificationType.INFORMATION)
                } catch (ex: Exception) {
                    notify(project, "Upload Failed", ex.message ?: "Unknown error occurred", NotificationType.ERROR)
                }
            }
        })
    }
}

class DownloadAction : BaseSftpAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val settings = SftpSettings.getInstance()
        val profile = settings.getActiveProfile() ?: run {
            notify(project, "Download Failed", "No active deployment profile selected.", NotificationType.WARNING)
            return
        }
        val server = settings.getServerForProfile(profile) ?: run {
            notify(project, "Download Failed", "No SSH server associated with active profile.", NotificationType.ERROR)
            return
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Downloading from ${server.host}...", false) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    val basePath = project.basePath ?: ""
                    val relativePath = if (virtualFile.path.startsWith(basePath)) {
                        virtualFile.path.removePrefix(basePath).removePrefix("/")
                    } else {
                        virtualFile.name
                    }
                    val remoteFilePath = "${profile.remotePath}/$relativePath"

                    SftpClient.download(server, remoteFilePath, virtualFile.path)
                    virtualFile.refresh(false, false)
                    notify(project, "Download Successful", "Downloaded ${virtualFile.name}", NotificationType.INFORMATION)
                } catch (ex: Exception) {
                    notify(project, "Download Failed", ex.message ?: "Unknown error occurred", NotificationType.ERROR)
                }
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
