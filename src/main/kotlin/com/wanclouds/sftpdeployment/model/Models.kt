package com.wanclouds.sftpdeployment.model

import com.intellij.util.xmlb.annotations.Transient
import java.util.UUID

enum class AuthType { PASSWORD, KEY_PAIR }

data class SshServer(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "New SSH Server",
    var host: String = "",
    var port: Int = 22,
    var user: String = "",
    var authType: AuthType = AuthType.PASSWORD,
    var privateKeyPath: String = ""
) {
    @get:Transient
    @set:Transient
    var secret: String?
        get() = SecureCredentialManager.getPassword(id, user)
        set(value) {
            SecureCredentialManager.savePassword(id, user, value)
        }
    
    override fun toString(): String = name
}

data class DeploymentProfile(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "Production Sync",
    var sshServerId: String = "",
    var remotePath: String = "/var/www/html",
    var autoUploadOnSave: Boolean = false
) {
    override fun toString(): String = name
}
