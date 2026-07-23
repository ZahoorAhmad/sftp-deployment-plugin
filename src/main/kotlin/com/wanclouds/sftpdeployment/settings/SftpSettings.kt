package com.wanclouds.sftpdeployment.settings

import com.wanclouds.sftpdeployment.model.DeploymentProfile
import com.wanclouds.sftpdeployment.model.SshServer
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "SftpDeploymentSettings", storages = [Storage("sftp_deployment.xml")])
class SftpSettings : PersistentStateComponent<SftpSettings> {
    var sshServers: MutableList<SshServer> = mutableListOf()
    var deployments: MutableList<DeploymentProfile> = mutableListOf()
    var activeDeploymentId: String = ""

    override fun getState(): SftpSettings = this
    override fun loadState(state: SftpSettings) = XmlSerializerUtil.copyBean(state, this)

    fun getActiveProfile(): DeploymentProfile? = deployments.find { it.id == activeDeploymentId }
    fun getServerForProfile(profile: DeploymentProfile?): SshServer? = sshServers.find { it.id == profile?.sshServerId }

    companion object {
        fun getInstance(): SftpSettings = service()
    }
}
