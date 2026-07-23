package com.wanclouds.sftpdeployment.model

import com.intellij.util.xmlb.annotations.Transient
import java.util.UUID

enum class AuthType { PASSWORD, KEY_PAIR }

class SshServer {
    var id: String = UUID.randomUUID().toString()
    var name: String = "New SSH Server"
    var host: String = ""
    var port: Int = 22
    var user: String = ""
    var authType: AuthType = AuthType.PASSWORD
    var privateKeyPath: String = ""

    @get:Transient
    @set:Transient
    var secret: String?
        get() = SecureCredentialManager.getPassword(id, user)
        set(value) {
            SecureCredentialManager.savePassword(id, user, value)
        }

    fun copy(): SshServer {
        val s = SshServer()
        s.id = this.id
        s.name = this.name
        s.host = this.host
        s.port = this.port
        s.user = this.user
        s.authType = this.authType
        s.privateKeyPath = this.privateKeyPath
        s.secret = this.secret
        return s
    }

    override fun toString(): String = if (name.isNotBlank()) name else "Unnamed Server"
}

class DeploymentProfile {
    var id: String = UUID.randomUUID().toString()
    var name: String = "New Profile"
    var sshServerId: String = ""
    var remotePath: String = "/var/www/html"
    var autoUploadOnSave: Boolean = false

    fun copy(): DeploymentProfile {
        val p = DeploymentProfile()
        p.id = this.id
        p.name = this.name
        p.sshServerId = this.sshServerId
        p.remotePath = this.remotePath
        p.autoUploadOnSave = this.autoUploadOnSave
        return p
    }

    override fun toString(): String = if (name.isNotBlank()) name else "Unnamed Profile"
}
