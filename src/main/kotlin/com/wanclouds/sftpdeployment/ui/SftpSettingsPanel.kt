package com.wanclouds.sftpdeployment.ui

import com.wanclouds.sftpdeployment.model.AuthType
import com.wanclouds.sftpdeployment.model.DeploymentProfile
import com.wanclouds.sftpdeployment.model.SshServer
import com.wanclouds.sftpdeployment.sftp.SftpClient
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel

class SftpSettingsPanel {
    val serverListModel = CollectionListModel<SshServer>()
    val serverList = JBList(serverListModel)
    
    val deploymentListModel = CollectionListModel<DeploymentProfile>()
    val deploymentList = JBList(deploymentListModel)

    val srvName = JBTextField()
    val srvHost = JBTextField()
    val srvPort = JBTextField().apply { text = "22" }
    val srvUser = JBTextField()
    val srvAuthType = ComboBox(AuthType.values())
    val srvKeyPath = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener("Select Private Key", null, null, FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor())
    }
    val srvSecret = JBPasswordField()

    val depName = JBTextField()
    val serverDropdown = ComboBox<SshServer>()
    val depRemotePath = JBTextField()
    val depAutoUpload = JBCheckBox("Auto-upload on save")

    val panel: DialogPanel = panel {
        collapsibleGroup("SSH Servers") {
            row {
                cell(ToolbarDecorator.createDecorator(serverList).setAddAction {
                    serverListModel.add(SshServer())
                    serverList.selectedIndex = serverListModel.size - 1
                }.createPanel()).align(Align.FILL)
                
                panel {
                    row("Name:") { cell(srvName).align(Align.FILL) }
                    row("Host:") { cell(srvHost).align(Align.FILL) }
                    row("Port:") { cell(srvPort) }
                    row("User:") { cell(srvUser).align(Align.FILL) }
                    row("Auth Type:") { cell(srvAuthType) }
                    row("Private Key:") { cell(srvKeyPath).align(Align.FILL) }
                    row("Password/Passphrase:") { cell(srvSecret).align(Align.FILL) }
                    row {
                        button("Test Connection") { testConnection() }
                    }
                }.align(Align.FILL)
            }
        }
        collapsibleGroup("Deployment Profiles") {
            row {
                cell(ToolbarDecorator.createDecorator(deploymentList).setAddAction {
                    deploymentListModel.add(DeploymentProfile())
                    deploymentList.selectedIndex = deploymentListModel.size - 1
                }.createPanel()).align(Align.FILL)
                
                panel {
                    row("Name:") { cell(depName).align(Align.FILL) }
                    row("SSH Server:") { cell(serverDropdown).align(Align.FILL) }
                    row("Remote Path:") { cell(depRemotePath).align(Align.FILL) }
                    row { cell(depAutoUpload) }
                }.align(Align.FILL)
            }
        }
    }

    init {
        serverList.addListSelectionListener { loadServer(serverList.selectedValue) }
        deploymentList.addListSelectionListener { loadDeployment(deploymentList.selectedValue) }
    }

    private fun loadServer(server: SshServer?) {
        if (server == null) return
        srvName.text = server.name
        srvHost.text = server.host
        srvPort.text = server.port.toString()
        srvUser.text = server.user
        srvAuthType.selectedItem = server.authType
        srvKeyPath.text = server.privateKeyPath
        srvSecret.text = server.secret ?: ""
    }

    private fun loadDeployment(dep: DeploymentProfile?) {
        if (dep == null) return
        depName.text = dep.name
        depRemotePath.text = dep.remotePath
        depAutoUpload.isSelected = dep.autoUploadOnSave
        serverDropdown.selectedItem = serverListModel.items.find { it.id == dep.sshServerId }
    }

    fun saveCurrentSelection() {
        serverList.selectedValue?.let {
            it.name = srvName.text
            it.host = srvHost.text
            it.port = srvPort.text.toIntOrNull() ?: 22
            it.user = srvUser.text
            it.authType = srvAuthType.selectedItem as AuthType
            it.privateKeyPath = srvKeyPath.text
            it.secret = String(srvSecret.password)
        }
        deploymentList.selectedValue?.let {
            it.name = depName.text
            it.sshServerId = (serverDropdown.selectedItem as? SshServer)?.id ?: ""
            it.remotePath = depRemotePath.text
            it.autoUploadOnSave = depAutoUpload.isSelected
        }
    }

    private fun testConnection() {
        val tempServer = SshServer(
            host = srvHost.text,
            port = srvPort.text.toIntOrNull() ?: 22,
            user = srvUser.text,
            authType = srvAuthType.selectedItem as AuthType,
            privateKeyPath = srvKeyPath.text
        ).apply { secret = String(srvSecret.password) }

        ProgressManager.getInstance().run(object : Task.Backgroundable(null, "Testing SSH Connection...", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                val result = SftpClient.testConnection(tempServer)
                ApplicationManager.getApplication().invokeLater {
                    val isOk = result.isSuccess
                    val msg = result.fold(onSuccess = { it }, onFailure = { it.message ?: "Unknown Error" })
                    
                    NotificationGroupManager.getInstance().getNotificationGroup("SFTP Connection Test")
                        .createNotification(
                            if (isOk) "Connection Successful" else "Connection Failed",
                            msg,
                            if (isOk) NotificationType.INFORMATION else NotificationType.ERROR
                        ).notify(null)
                }
            }
        })
    }
}
