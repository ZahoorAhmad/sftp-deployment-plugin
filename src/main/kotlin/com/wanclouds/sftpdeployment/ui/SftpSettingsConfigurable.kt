package com.wanclouds.sftpdeployment.ui

import com.wanclouds.sftpdeployment.settings.SftpSettings
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class SftpSettingsConfigurable(private val project: Project) : SearchableConfigurable {
    private var uiComponent: SftpSettingsPanel? = null

    override fun getId(): String = "com.wanclouds.sftpdeployment.ui.SftpSettingsConfigurable"
    override fun getDisplayName(): String = "SFTP Deployment"

    override fun createComponent(): JComponent {
        uiComponent = SftpSettingsPanel(project)
        return uiComponent!!.panel
    }

    override fun isModified(): Boolean = true

    override fun apply() {
        val settings = SftpSettings.getInstance(project)
        val ui = uiComponent ?: return

        ui.saveCurrentSelection()
        settings.sshServers = ui.serverListModel.items.map { it.copy() }.toMutableList()
        settings.deployments = ui.deploymentListModel.items.map { it.copy() }.toMutableList()
        settings.activeDeploymentId = ui.getSelectedDefaultProfileId()
    }

    override fun reset() {
        val settings = SftpSettings.getInstance(project)
        val ui = uiComponent ?: return

        ui.serverListModel.replaceAll(settings.sshServers.map { it.copy() })
        ui.deploymentListModel.replaceAll(settings.deployments.map { it.copy() })

        ui.refreshServerDropdown()
        ui.refreshDefaultProfileDropdown(settings.activeDeploymentId)
    }

    override fun disposeUIResources() {
        uiComponent = null
    }
}
