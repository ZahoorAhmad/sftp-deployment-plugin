package com.wanclouds.sftpdeployment.ui

import com.wanclouds.sftpdeployment.model.AuthType
import com.wanclouds.sftpdeployment.model.DeploymentProfile
import com.wanclouds.sftpdeployment.model.SshServer
import com.wanclouds.sftpdeployment.sftp.SftpClient
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

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

    private var isUpdatingUi = false

    val panel: DialogPanel = panel {
        collapsibleGroup("SSH Servers") {
            row {
                cell(ToolbarDecorator.createDecorator(serverList)
                    .setAddAction {
                        val newServer = SshServer()
                        serverListModel.add(newServer)
                        serverList.setSelectedValue(newServer, true)
                        refreshServerDropdown()
                    }
                    .setRemoveAction {
                        val selected = serverList.selectedValue
                        if (selected != null) {
                            serverListModel.remove(selected)
                            refreshServerDropdown()
                        }
                    }
                    .createPanel()).align(Align.FILL)

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
                cell(ToolbarDecorator.createDecorator(deploymentList)
                    .setAddAction {
                        val newDep = DeploymentProfile()
                        deploymentListModel.add(newDep)
                        deploymentList.setSelectedValue(newDep, true)
                    }
                    .setRemoveAction {
                        val selected = deploymentList.selectedValue
                        if (selected != null) {
                            deploymentListModel.remove(selected)
                        }
                    }
                    .createPanel()).align(Align.FILL)

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
        serverList.addListSelectionListener {
            if (!isUpdatingUi) loadServer(serverList.selectedValue)
        }
        deploymentList.addListSelectionListener {
            if (!isUpdatingUi) loadDeployment(deploymentList.selectedValue)
        }

        bindLiveFields()
    }

    private fun bindLiveFields() {
        val listener = object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = updateCurrentServer()
            override fun removeUpdate(e: DocumentEvent?) = updateCurrentServer()
            override fun changedUpdate(e: DocumentEvent?) = updateCurrentServer()
        }
        srvName.document.addDocumentListener(listener)
        srvHost.document.addDocumentListener(listener)
        srvPort.document.addDocumentListener(listener)
        srvUser.document.addDocumentListener(listener)
        srvSecret.document.addDocumentListener(listener)
        srvKeyPath.textField.document.addDocumentListener(listener)
        srvAuthType.addActionListener { updateCurrentServer() }
    }

    private fun updateCurrentServer() {
        if (isUpdatingUi) return
        val current = serverList.selectedValue ?: return
        current.name = srvName.text
        current.host = srvHost.text
        current.port = srvPort.text.toIntOrNull() ?: 22
        current.user = srvUser.text
        current.authType = srvAuthType.selectedItem as AuthType
        current.privateKeyPath = srvKeyPath.text
        current.secret = String(srvSecret.password)
        serverList.repaint()
        refreshServerDropdown()
    }

    fun refreshServerDropdown() {
        val currentSelected = serverDropdown.selectedItem
        serverDropdown.removeAllItems()
        serverListModel.items.forEach { serverDropdown.addItem(it) }
        if (currentSelected in serverListModel.items) {
            serverDropdown.selectedItem = currentSelected
        }
    }

    private fun loadServer(server: SshServer?) {
        isUpdatingUi = true
        try {
            if (server == null) {
                srvName.text = ""
                srvHost.text = ""
                srvPort.text = "22"
                srvUser.text = ""
                srvKeyPath.text = ""
                srvSecret.text = ""
                return
            }
            srvName.text = server.name
            srvHost.text = server.host
            srvPort.text = server.port.toString()
            srvUser.text = server.user
            srvAuthType.selectedItem = server.authType
            srvKeyPath.text = server.privateKeyPath
            srvSecret.text = server.secret ?: ""
        } finally {
            isUpdatingUi = false
        }
    }

    private fun loadDeployment(dep: DeploymentProfile?) {
        isUpdatingUi = true
        try {
            if (dep == null) {
                depName.text = ""
                depRemotePath.text = ""
                depAutoUpload.isSelected = false
                return
            }
            depName.text = dep.name
            depRemotePath.text = dep.remotePath
            depAutoUpload.isSelected = dep.autoUploadOnSave
            serverDropdown.selectedItem = serverListModel.items.find { it.id == dep.sshServerId }
        } finally {
            isUpdatingUi = false
        }
    }

    fun saveCurrentSelection() {
        updateCurrentServer()
        deploymentList.selectedValue?.let {
            it.name = depName.text
            it.sshServerId = (serverDropdown.selectedItem as? SshServer)?.id ?: ""
            it.remotePath = depRemotePath.text
            it.autoUploadOnSave = depAutoUpload.isSelected
        }
    }

    private fun testConnection() {
        val host = srvHost.text.trim()
        if (host.isEmpty()) {
            Messages.showErrorDialog("Please enter a valid Host IP or hostname.", "Connection Failed")
            return
        }

        val tempServer = SshServer().apply {
            this.host = host
            this.port = srvPort.text.toIntOrNull() ?: 22
            this.user = srvUser.text.trim()
            this.authType = srvAuthType.selectedItem as AuthType
            this.privateKeyPath = srvKeyPath.text.trim()
            this.secret = String(srvSecret.password)
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(null, "Testing SSH Connection...", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                val result = SftpClient.testConnection(tempServer)
                ApplicationManager.getApplication().invokeLater {
                    result.fold(
                        onSuccess = { msg ->
                            Messages.showInfoMessage(msg, "Connection Test Succeeded")
                        },
                        onFailure = { err ->
                            Messages.showErrorDialog(err.message ?: "Failed to connect to $host", "Connection Test Failed")
                        }
                    )
                }
            }
        })
    }
}
