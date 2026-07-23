# SFTP Deployment Plugin for IntelliJ Platform

A powerful SFTP deployment plugin for PyCharm and IntelliJ Community Edition that brings remote deployment features to non-professional editions.

## Features

- **Multiple SSH Server Profiles** - Define multiple SSH servers with securely stored credentials
- **Password & SSH Key Authentication** - Support for both password and private key authentication (with passphrase)
- **Secure Credential Storage** - Uses IntelliJ PasswordSafe integration with native OS keychain
- **Multiple Deployment Profiles** - Create multiple deployment configurations pointing to different remote paths
- **Auto-Upload on Save** - Automatically upload files when you save (Ctrl+S / Cmd+S) if enabled per profile
- **Upload/Download Actions** - Right-click context menu actions in Project View for manual uploads and downloads
- **Connection Testing** - Test SSH connections with instant feedback and error reporting
- **Modern UI** - Built with Kotlin UI DSL for a native IntelliJ look and feel

## Installation

1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Run `./gradlew build` to build the plugin
4. The plugin JAR will be in `build/distributions/`
5. Install via `File > Settings > Plugins > Install Plugin from Disk`

## Usage

### Setting Up SSH Servers

1. Go to `Tools > SFTP Deployment`
2. Click **Add** under "SSH Servers"
3. Configure your server:
   - **Server Name**: A friendly name for this server
   - **Host**: The hostname or IP address
   - **Port**: SSH port (default: 22)
   - **User**: SSH username
   - **Auth Type**: Choose "PASSWORD" or "KEY_PAIR"
   - If using KEY_PAIR, specify the private key file path
   - If using PASSWORD, enter your password (it will be securely stored)
4. Click **Test Connection** to verify
5. Click **Save**

### Creating Deployment Profiles

1. In the same settings page, click **Add** under "Deployment Profiles"
2. Configure your profile:
   - **Profile Name**: A friendly name
   - **SSH Server**: Select the SSH server to use
   - **Remote Path**: The directory path on the remote server where files will be deployed
   - **Auto-upload on Save**: Check to automatically upload modified files
3. Click **Save**

### Using Deployment

#### Manual Upload/Download
- Right-click any file in the Project View
- Select "Upload to Remote" or "Download from Remote"
- The file will be transferred to/from the configured remote path

#### Auto-Upload
- If "Auto-upload on Save" is enabled for the active profile
- Files will automatically upload when you press Ctrl+S (or Cmd+S on Mac)

## Architecture

### Core Components

- **Models.kt** - Data models for `SshServer`, `DeploymentProfile`, and `AuthType`
- **SecureCredentialManager.kt** - Handles secure storage/retrieval of credentials using PasswordSafe
- **SftpSettings.kt** - PersistentStateComponent for plugin settings
- **SftpClient.kt** - Core SFTP operations using JSch library
- **SftpSettingsPanel.kt** - Master-Detail UI for settings management
- **SftpSettingsConfigurable.kt** - Integration with IntelliJ Settings
- **Actions.kt** - UploadAction, DownloadAction, and AutoUploadListener

### Dependencies

- **JSch 0.2.16** - Modern fork for SFTP/SSH operations
- **IntelliJ Platform SDK 2024.1**
- **Kotlin stdlib**

## Security

- All passwords and passphrases are stored using IntelliJ's PasswordSafe API, which integrates with the native OS keychain
- In-memory credentials are never persisted to disk
- SSH configurations are stored in the plugin's standard settings storage (XML format)

## Troubleshooting

### Connection Test Fails
- Verify the host is reachable: `ping hostname`
- Check SSH credentials (username/password or key path)
- Ensure the SSH port is not blocked by firewall
- For key authentication, verify the private key permissions: `chmod 600 ~/.ssh/id_rsa`

### Passphrase Not Saved
- Passphrases for private keys are stored securely using PasswordSafe
- On first use, you may need to enter it again

### Files Not Uploading
- Verify the remote path exists or has write permissions
- Check that the active profile is correctly configured
- Review the notification messages for specific errors

## License

Copyright © 2024 WanClouds. All rights reserved.

## Support

For issues, feature requests, or contributions, please visit the project repository.
