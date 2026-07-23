package com.wanclouds.sftpdeployment.ui

import com.wanclouds.sftpdeployment.settings.SftpSettings
import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.JComponent

class SftpSettingsConfigurable : SearchableConfigurable {
    private var uiComponent: SftpSettingsPanel? = null

    override fun getId(): String = "com.wanclouds.sftpdeployment.ui.SftpSettingsConfigurable"
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
        settings.sshServers = ui.serverListModel.items.map { it.copy() }.toMutableList()
        settings.deployments = ui.deploymentListModel.items.map { it.copy() }.toMutableList()
        settings.activeDeploymentId = ui.deploymentList.selectedValue?.id ?: ""
    }

    override fun reset() {
        val settings = SftpSettings.getInstance()
        val ui = uiComponent ?: return

        ui.serverListModel.replaceAll(settings.sshServers.map { it.copy() })
        ui.deploymentListModel.replaceAll(settings.deployments.map { it.copy() })

        ui.refreshServerDropdown()
        if (ui.serverListModel.size > 0) {
            ui.serverList.selectedIndex = 0
        }
        if (ui.deploymentListModel.size > 0) {
            ui.deploymentList.selectedIndex = 0
        }
    }

    override fun disposeUIResources() {
        uiComponent = null
    }
}
