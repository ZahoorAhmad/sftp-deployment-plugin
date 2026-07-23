# SFTP Deployment Plugin - Architecture Guide

## Overview

The SFTP Deployment Plugin is a comprehensive IntelliJ Platform plugin that brings professional-grade SFTP deployment features to PyCharm and IntelliJ Community Edition. It's built using Kotlin with the IntelliJ Platform Plugin SDK.

## Architecture Layers

### 1. Data Layer

#### Models (`model/Models.kt`)
- **AuthType**: Enum supporting PASSWORD and KEY_PAIR authentication
- **SshServer**: Represents an SSH configuration
  - Stores: id, name, host, port, user, authType, privateKeyPath
  - Methods: getSecret(), setSecret() delegate to SecureCredentialManager
- **DeploymentProfile**: Represents a deployment target
  - Stores: id, name, sshServerId, remotePath, autoUploadOnSave
  - Links a server to a specific deployment configuration

#### Secure Storage (`model/SecureCredentialManager.kt`)
- Singleton object managing credential storage
- Uses IntelliJ's `PasswordSafe` API for OS keychain integration
- Methods:
  - `saveCredential(server, credential)`: Stores password/passphrase securely
  - `getCredential(server)`: Retrieves stored credential
  - `removeCredential(server)`: Deletes credential

### 2. Settings Layer

#### Settings Component (`settings/SftpSettings.kt`)
- Implements `PersistentStateComponent<SftpSettings>`
- Persists to `sftp_deployment.xml` in project settings
- Manages collections of:
  - `sshServers`: List of SSH configurations
  - `deploymentProfiles`: List of deployment targets
  - `activeProfileId`: Currently selected deployment profile
- Provides CRUD operations:
  - addServer(), removeServer(), updateServer(), getServer()
  - addProfile(), removeProfile(), updateProfile(), getProfile()
  - getActiveProfile()
- Registered as ApplicationService for application-level availability

### 3. Business Logic Layer

#### SFTP Operations (`sftp/SftpClient.kt`)
- Singleton object handling all SFTP operations
- Provides sealed Result<T> class:
  - Success<T>: Contains operation result
  - Error<T>: Contains exception and error message
- Public methods:
  - `testConnection(server)`: Validates SSH connectivity
  - `upload(server, localPath, remotePath)`: Transfers file to remote
  - `download(server, remotePath, localPath)`: Transfers file from remote
- Private helper:
  - `createSession(server)`: Sets up JSch session with auth configuration
- JSch Configuration:
  - PASSWORD auth: Uses session.setPassword()
  - KEY_PAIR auth: Uses jsch.addIdentity() with optional passphrase
  - Security: Disables StrictHostKeyChecking for ease of use

### 4. Presentation Layer

#### Settings UI (`ui/SftpSettingsPanel.kt`)
- Master-Detail layout with three panels:
  - **Left Panel**: SSH Server list
  - **Middle Panel**: Server detail form
  - **Right Panel (Top)**: Deployment Profile list
  - **Right Panel (Bottom)**: Profile detail form

- Server Detail Form:
  - TextFields: name, host, port, user
  - ComboBox: Auth type selector
  - File chooser: Private key path
  - Password field: For passwords/passphrases
  - Actions: Test Connection, Save buttons

- Profile Detail Form:
  - TextFields: name, remote path
  - ComboBox: Server selector
  - Checkbox: Auto-upload on save toggle
  - Action: Save button

- Event Handling:
  - Selection listeners populate form fields
  - Add/Remove buttons modify lists
  - Save buttons persist to settings
  - Test Connection triggers background task

#### Settings Configurable (`ui/SftpSettingsConfigurable.kt`)
- Implements `SearchableConfigurable`
- Registered in plugin.xml as `applicationConfigurable`
- Creates SearchableConfigurable UI component
- Integrates with Tools menu in IntelliJ settings

### 5. Action Layer

#### User Actions (`actions/Actions.kt`)

**UploadAction**
- Extends `AnAction`
- Triggered from: Project View context menu (right-click file)
- Workflow:
  1. Get selected virtual file
  2. Check for active profile
  3. Get SSH server for profile
  4. Execute upload on thread pool
  5. Show success/error notification

**DownloadAction**
- Extends `AnAction`
- Triggered from: Project View context menu
- Workflow:
  1. Get selected virtual file
  2. Check for active profile
  3. Get SSH server for profile
  4. Execute download on thread pool
  5. Show success/error notification
  6. Refresh file to show updated content

**AutoUploadListener**
- Implements `FileDocumentManagerListener`
- Hooked into document save event
- Workflow:
  1. Check if auto-upload enabled in active profile
  2. Get current document's file
  3. Verify it's in local file system
  4. Execute upload on thread pool
  5. User doesn't see notification (silent background operation)

### 6. Plugin Integration

#### Plugin Manifest (`resources/META-INF/plugin.xml`)

Registrations:
```xml
<!-- Service: Application-level settings -->
<applicationService serviceImplementation="SftpSettings"/>

<!-- UI: Settings page -->
<applicationConfigurable parentId="tools" instance="SftpSettingsConfigurable"/>

<!-- Notification: User feedback -->
<notificationGroup id="SFTP Deployment" displayType="BALLOON"/>

<!-- Listener: Auto-upload on save -->
<fileDocumentManagerListener implementation="AutoUploadListener"/>

<!-- Actions: Context menu entries -->
<action id="SftpUploadAction" class="UploadAction"/>
<action id="SftpDownloadAction" class="DownloadAction"/>
```

#### Messages Bundle
- `resources/messages/SftpDeploymentBundle.properties`
- Contains all UI strings for internationalization support

## Data Flow Diagrams

### Connection Test Flow
```
User clicks "Test Connection"
      ↓
SftpSettingsPanel.testConnection()
      ↓
ProgressManager.run(Task.Backgroundable)
      ↓
SftpClient.testConnection(server)
      ↓
JSch.getSession(host, port, user)
      ↓
session.setPassword() or jsch.addIdentity()
      ↓
session.connect(5000ms)
      ↓
session.disconnect()
      ↓
Notification: Success/Error
```

### File Upload Flow
```
User right-clicks file → selects "Upload to Remote"
      ↓
UploadAction.actionPerformed()
      ↓
Get active profile & SSH server
      ↓
ApplicationManager.executeOnPooledThread()
      ↓
SftpClient.upload(server, localPath, remotePath)
      ↓
JSch.getSession() → authenticate → open SFTP channel
      ↓
channel.put(localFile, remotePath)
      ↓
Notification: Success/Error
```

### Auto-Upload Flow
```
User saves file (Ctrl+S)
      ↓
DocumentManager.beforeDocumentSaving()
      ↓
AutoUploadListener.beforeDocumentSaving()
      ↓
Check: autoUploadOnSave enabled? → if not, return
      ↓
ApplicationManager.executeOnPooledThread()
      ↓
SftpClient.upload() [silent, no notification]
```

## Security Considerations

### Credential Storage
- **PasswordSafe Integration**: IntelliJ's PasswordSafe delegates to OS keychains
  - macOS: Keychain
  - Windows: Credential Manager
  - Linux: Memory or system keyring (depends on config)
- **Transient Memory**: Credentials loaded only when needed
- **No Persistence**: Passwords never written to XML or disk

### Connection Security
- **StrictHostKeyChecking Disabled**: For user convenience (aware security trade-off)
- **Could be improved**: Implement known_hosts verification for enterprise use

### File Operations
- **Local File Validation**: Checks file exists before upload
- **Remote Path Handling**: Ensures directory exists before file operation
- **Error Handling**: Graceful error reporting without credential leaks

## Extension Points

### Making the Plugin Extensible

1. **Custom Authentication Providers**
   - Add AuthProvider interface
   - Allow plugins to contribute auth types

2. **File Transfer Filters**
   - Add FileTransferFilter interface
   - Support .gitignore-style exclude patterns

3. **Pre/Post-Upload Hooks**
   - Add listener interface for operation events
   - Allow custom processing

4. **Cloud Provider Support**
   - Add CloudProvider abstraction
   - Implement AWS S3, Azure, Google Cloud adapters

## Performance Optimization

### Current Optimizations
- Background thread execution (UI never blocks)
- Connection timeout (5 seconds for tests)
- Lazy credential loading

### Future Optimizations
- Connection pooling (reuse sessions across operations)
- Incremental uploads (check timestamps/CRC)
- Batch operations (upload multiple files once)

## Error Handling Strategy

### SFTP Operations
- Catch `JSchException` for SSH/SFTP errors
- Catch generic `Exception` for unexpected failures
- Return Result sealed class for controlled error handling

### UI Operations
- Log all exceptions
- Show user-friendly error messages in notifications
- Don't crash on credential issues

### File Operations
- Validate local files before operations
- Check write permissions on remote
- Recover from partial transfers

## Testing Strategy

### Manual Testing Checklist
- [ ] Settings UI: Create, edit, delete servers and profiles
- [ ] Authentication: Test password and key-pair auth
- [ ] Connection Test: Verify working and failing cases
- [ ] Upload: Manual file upload via context menu
- [ ] Download: Manual file download via context menu
- [ ] Auto-Upload: Verify files upload automatically on save
- [ ] Notifications: Confirm success/error messages appear
- [ ] Credentials: Verify passwords stored securely

### Automated Testing (Future)
- Write integration tests for SftpClient
- Mock JSch for unit tests
- Test Settings persistence
- Test UI components in isolation

## Deployment & Distribution

### Build Artifacts
- `build/distributions/sftp-deployment-plugin-1.0.0.jar`

### Installation Methods
1. File > Settings > Plugins > Install from Disk
2. Manual copy to plugins directory
3. JetBrains Plugin Marketplace (pending approval)

### Supported IDEs
- PyCharm Community Edition 2024.1+
- IntelliJ IDEA Community Edition 2024.1+
- Any JetBrains IDE on 2024.1+

---

This architecture provides a clean separation of concerns, making the plugin maintainable, testable, and extensible for future enhancements.
