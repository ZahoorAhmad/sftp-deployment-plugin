package com.wanclouds.sftpdeployment.sftp

import com.wanclouds.sftpdeployment.model.AuthType
import com.wanclouds.sftpdeployment.model.SshServer
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import java.io.File

object SftpClient {
    fun testConnection(server: SshServer): Result<String> {
        return try {
            executeSftp(server) { sftp -> sftp.pwd() }
            Result.success("Successfully connected to ${server.host}:${server.port}!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun upload(server: SshServer, localFile: File, remotePath: String, relativePath: String) {
        executeSftp(server) { sftp ->
            val targetDir = "$remotePath/$relativePath".substringBeforeLast("/")
            createDirectories(sftp, targetDir)
            sftp.put(localFile.absolutePath, "$remotePath/$relativePath")
        }
    }

    fun download(server: SshServer, remoteFilePath: String, localDest: String) {
        executeSftp(server) { sftp ->
            sftp.get(remoteFilePath, localDest)
        }
    }

    private fun executeSftp(server: SshServer, action: (ChannelSftp) -> Unit) {
        val jsch = JSch()
        val secretStr: String? = server.secret

        if (server.authType == AuthType.KEY_PAIR && server.privateKeyPath.isNotEmpty()) {
            val passphraseBytes: ByteArray? = secretStr?.toByteArray()
            if (passphraseBytes != null) {
                jsch.addIdentity(server.privateKeyPath, passphraseBytes)
            } else {
                jsch.addIdentity(server.privateKeyPath)
            }
        }

        val session = jsch.getSession(server.user, server.host, server.port)
        if (server.authType == AuthType.PASSWORD && secretStr != null) {
            session.setPassword(secretStr)
        }
        
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect(10000)
        
        val channel = session.openChannel("sftp") as ChannelSftp
        channel.connect(10000)
        
        try { 
            action(channel) 
        } finally {
            channel.disconnect()
            session.disconnect()
        }
    }

    private fun createDirectories(sftp: ChannelSftp, path: String) {
        val folders = path.split("/").filter { it.isNotEmpty() }
        var currentPath = ""
        for (folder in folders) {
            currentPath += "/$folder"
            try {
                sftp.cd(currentPath)
            } catch (e: Exception) {
                sftp.mkdir(currentPath)
            }
        }
    }
}
