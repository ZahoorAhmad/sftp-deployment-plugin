package com.wanclouds.sftpdeployment.model

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe

object SecureCredentialManager {
    private fun createAttributes(profileId: String, userName: String): CredentialAttributes {
        return CredentialAttributes("SftpDeploymentPlugin_$profileId", userName)
    }

    fun savePassword(profileId: String, userName: String, password: String?) {
        val attributes = createAttributes(profileId, userName)
        val credentials = if (password.isNullOrEmpty()) null else Credentials(userName, password)
        PasswordSafe.instance.set(attributes, credentials)
    }

    fun getPassword(profileId: String, userName: String): String? {
        val attributes = createAttributes(profileId, userName)
        return PasswordSafe.instance.getPassword(attributes)
    }
}
