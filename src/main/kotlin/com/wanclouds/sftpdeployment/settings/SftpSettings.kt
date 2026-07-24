package com.wanclouds.sftpdeployment.settings

import com.wanclouds.sftpdeployment.model.DeploymentProfile
import com.wanclouds.sftpdeployment.model.SshServer
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "SftpDeploymentProjectSettings",
    storages = [Storage("sftp_deployment_project.xml")]
)
class SftpSettings : PersistentStateComponent<SftpSettings> {
    var sshServers: MutableList<SshServer> = mutableListOf()
    var deployments: MutableList<DeploymentProfile> = mutableListOf()
    var activeDeploymentId: String = ""

    override fun getState(): SftpSettings = this
    override fun loadState(state: SftpSettings) = XmlSerializerUtil.copyBean(state, this)

    fun getActiveProfile(): DeploymentProfile? {
        return deployments.find { it.id == activeDeploymentId } ?: deployments.firstOrNull()
    }

    fun getServerForProfile(profile: DeploymentProfile?): SshServer? {
        val targetProfile = profile ?: getActiveProfile()
        return sshServers.find { it.id == targetProfile?.sshServerId }
    }

    companion object {
        fun getInstance(project: Project): SftpSettings {
            return project.getService(SftpSettings::class.java)
        }

        // Fallback for global context
        fun getInstance(): SftpSettings {
            return com.intellij.openapi.application.ApplicationManager.getApplication().getService(SftpSettings::class.java)
        }
    }
}
