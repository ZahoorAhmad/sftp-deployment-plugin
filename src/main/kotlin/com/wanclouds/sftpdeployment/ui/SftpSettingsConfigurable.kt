package com.wanclouds.sftpdeployment.ui

import com.wanclouds.sftpdeployment.settings.SftpSettings
import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.JComponent

class SftpSettingsConfigurable : SearchableConfigurable {
    private var uiComponent: SftpSettingsPanel? = null

    override fun getId(): String = "preferences.SftpDeployment"
    override fun getDisplayName(): String = "SFTP Deployment"

    override fun createComponent(): JComponent {
        uiComponent = SftpSettingsPanel()
        return uiComponent!!.panel
    }

    override fun isModified(): Boolean = true

    override fun apply() {
        val settings = SftpSettings.getInstance()
        val ui = uiComponent ?: return
        
        ui.saveCurrentSelection()
        settings.sshServers = ui.serverListModel.items.toMutableList()
        settings.deployments = ui.deploymentListModel.items.toMutableList()
        settings.activeDeploymentId = ui.deploymentList.selectedValue?.id ?: ""
    }

    override fun reset() {
        val settings = SftpSettings.getInstance()
        val ui = uiComponent ?: return

        ui.serverListModel.replaceAll(settings.sshServers.map { it.copy() })
        ui.deploymentListModel.replaceAll(settings.deployments.map { it.copy() })
        
        ui.serverDropdown.removeAllItems()
        ui.serverListModel.items.forEach { ui.serverDropdown.addItem(it) }
    }

    override fun disposeUIResources() {
        uiComponent = null
    }
}
