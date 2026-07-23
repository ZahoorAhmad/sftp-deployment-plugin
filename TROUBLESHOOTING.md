# Troubleshooting Guide

## Build Issues

### Error: "Cannot find JDK 17"

**Problem**: Build fails with JDK version error

**Solution**:
```bash
# Set JAVA_HOME to JDK 17 or higher
export JAVA_HOME=/path/to/jdk17  # macOS/Linux
# or
set JAVA_HOME=C:\path\to\jdk17   # Windows

# Verify Java version
java -version
```

### Error: "Plugin descriptor is invalid"

**Problem**: `plugin.xml` parsing error

**Solution**:
1. Check `plugin.xml` for XML syntax errors
2. Ensure all required fields are present:
   - `<id>com.wanclouds.sftpdeployment</id>`
   - `<name>SFTP Deployment</name>`
   - `<vendor>...</vendor>`
   - `<description>...</description>`
3. Validate XML structure

### Error: "Cannot resolve JSch dependency"

**Problem**: JSch library not found

**Solution**:
```kotlin
// In build.gradle.kts, verify the dependency:
implementation("com.github.mwiede:jsch:0.2.16")

// Then run:
./gradlew build --refresh-dependencies
```

### Build Hangs or Timeout

**Problem**: Build process hangs indefinitely

**Solution**:
```bash
# Kill Gradle daemon
./gradlew --stop

# Force clean rebuild with increased memory
./gradlew build -Xmx2g --refresh-dependencies
```

### "Duplicate plugin ID" or "Plugin already installed"

**Problem**: Old version conflicts with new version

**Solution**:
1. Uninstall old version: Settings > Plugins > Search > Right-click > Uninstall
2. Restart IntelliJ
3. Install new version
4. Or use `./gradlew clean` to remove build artifacts

---

## Runtime Issues

### Plugin Not Appearing in Settings

**Problem**: "Tools > SFTP Deployment" doesn't appear

**Solution**:
1. Check plugin is installed: Settings > Plugins > Search "SFTP"
2. If not installed, use "Install Plugin from Disk" with JAR
3. Restart IntelliJ
4. Check Help > Show Log in Explorer for errors
5. Look for entry in `idea.log`:
   ```
   Cannot load plugin: com.wanclouds.sftpdeployment
   ```

### Settings UI Doesn't Load

**Problem**: Opening Tools > SFTP Deployment shows blank or error

**Solution**:
1. Check for missing UI dependencies
2. Verify panel creation code doesn't have initialization errors
3. Enable debug logging: Help > Diagnostic Tools > Debug Log > `#com.wanclouds.sftpdeployment`
4. Restart and check logs

### Connection Test Always Fails

**Problem**: Even valid credentials fail to connect

**Solution**:
1. Test SSH manually first:
   ```bash
   ssh -v user@host -p 22
   ```
2. Verify network connectivity:
   ```bash
   ping hostname
   telnet hostname 22
   ```
3. Check firewall rules allow port 22
4. Verify username is correct
5. For key auth, check file permissions:
   ```bash
   chmod 600 ~/.ssh/id_rsa
   ```

### Credentials Not Saving

**Problem**: Password/passphrase resets after restart

**Solution**:
1. Verify PasswordSafe is working:
   - Settings > Appearance & Behavior > System Settings > Passwords
   - Try "Reset Master Password" if OS keychain is corrupted

2. On Linux, ensure keyring daemon is running:
   ```bash
   ps aux | grep keyring
   # Should see gnome-keyring-daemon or similar
   ```

3. Check storage permissions:
   ```bash
   ls -la ~/.config/JetBrains/
   # Should be writable
   chmod 700 ~/.config/JetBrains/
   ```

---

## Upload/Download Issues

### Upload Fails: "Remote path does not exist"

**Problem**: File won't upload even with correct credentials

**Solution**:
1. Create the remote directory:
   ```bash
   ssh user@host mkdir -p /path/to/directory
   ```
2. Verify directory exists:
   ```bash
   ssh user@host ls -la /path/to/
   ```
3. Check permissions (directory writable):
   ```bash
   ssh user@host chmod 755 /path/to/directory
   ```
4. Try uploading to `/tmp` first to debug

### Upload Fails: "Permission denied"

**Problem**: File won't upload due to permissions

**Solution**:
1. Check remote directory permissions:
   ```bash
   ssh user@host ls -ld /path/to/directory
   # Output: drwxrwxr-x means user can write
   ```
2. Fix permissions if needed:
   ```bash
   ssh user@host chmod 755 /path/to/directory
   ```
3. Verify user owns or can write to directory
4. Check disk space: `ssh user@host df -h`

### Download Fails: "File not found"

**Problem**: Remote file doesn't exist or wrong path

**Solution**:
1. Verify remote file exists:
   ```bash
   ssh user@host ls -la /path/to/remote_file.txt
   ```
2. Check file path in settings (exact case sensitivity on Linux)
3. Verify user has read permissions:
   ```bash
   ssh user@host cat /path/to/remote_file.txt
   ```

### Upload/Download is Slow

**Problem**: File transfer takes too long

**Solution**:
1. Check network speed:
   ```bash
   ping -c 5 hostname  # Check latency
   ```
2. Verify no SSH port forwarding delays
3. Try direct connection without tunnels
4. Split large files and upload separately
5. Check if auto-upload is causing contention

---

## UI/UX Issues

### Buttons/Fields Don't Respond

**Problem**: UI elements are unresponsive or frozen

**Solution**:
1. Check if background task is running: Look for progress indicator
2. If frozen for >30 seconds, likely infinite loop
3. Check logs: Help > Show Log in Explorer
4. Try restarting IntelliJ
5. See "Build Issues" > "Build Hangs" solution

### Form Fields Lose Values

**Problem**: Values entered in settings don't persist

**Solution**:
1. Click **Save** button (required!)
2. Check if form validation rejected values
3. Restart IntelliJ to confirm persistence
4. Check project `.idea/` directory permissions

### Notifications Don't Appear

**Problem**: Success/error messages not showing

**Solution**:
1. Check notification settings: Settings > Notifications > SFTP Deployment
2. May be grouped or hidden: Check notification history at top right
3. Check Do Not Disturb mode: Disable in notification center
4. Try restarting IDE

---

## SSH Key Issues

### "Permission denied (publickey)"

**Problem**: SSH key authentication fails

**Solution**:
1. Verify key file permissions:
   ```bash
   ls -l ~/.ssh/id_rsa
   # Should be -rw------- (600)
   chmod 600 ~/.ssh/id_rsa  # Fix permissions
   ```
2. Verify key is added to ssh-agent:
   ```bash
   ssh-add -l  # List keys
   ssh-add ~/.ssh/id_rsa  # Add key
   ```
3. Check server has public key:
   ```bash
   ssh user@host cat ~/.ssh/authorized_keys
   # Should contain your public key
   ```
4. Try different key algorithm:
   - Test with password auth first
   - Try RSA key instead of ED25519 or vice versa

### Passphrase Not Working

**Problem**: SSH key passphrase rejected

**Solution**:
1. Test passphrase manually:
   ```bash
   ssh-keygen -p -f ~/.ssh/id_rsa  # Change passphrase
   ```
2. Clear saved passphrase:
   - Open Settings > SFTP Deployment
   - Clear the password field
   - Re-enter passphrase
   - Click Save
3. Check keychain for old passphrase:
   - macOS: Keychain Access > Search for SSH
   - Delete old entries and re-save

---

## Auto-Upload Issues

### Auto-Upload Not Triggering

**Problem**: Files don't upload automatically on save

**Solution**:
1. Verify "Auto-upload on Save" is checked in profile
2. Verify profile is set as active:
   - Should be highlighted in profile list
3. Save using Ctrl+S (make sure it actually saves)
4. Check if file is in project root (not excluded)
5. Check notification/logs if there was a failure
6. Restart IDE to clear any stale state

### Auto-Upload Uploads Wrong File

**Problem**: Wrong file gets uploaded on save

**Solution**:
1. Check "Remote Path" in profile (correct directory?)
2. Verify correct profile is active
3. Check if symlinks are causing confusion
4. Enable notifications to see what's uploaded
5. Monitor server: `watch ls -la /remote/path/`

### Auto-Upload Slowing Down IDE

**Problem**: IDE sluggish during auto-uploads

**Solution**:
1. Disable auto-upload, use manual upload instead
2. Check if uploads are failing and retrying
3. Verify network connection is stable
4. Monitor system resources: CPU, disk, memory
5. Try background thread pool size (Gradle setting):
   ```properties
   org.gradle.workers.max=2
   ```

---

## Credential/Security Issues

### Keychain/Credential Manager Not Available

**Problem**: PasswordSafe can't access native keychain

**Solution**:
1. On Linux, install and start keyring daemon:
   ```bash
   sudo apt-get install gnome-keyring
   # Or your distro's keyring package
   ```
2. On macOS, verify Keychain is accessible:
   - System Preferences > Security & Privacy > Keychain
3. On Windows, verify Credential Manager:
   - Control Panel > Credential Manager
4. Reset IntelliJ PasswordSafe:
   - Settings > Appearance & Behavior > System Settings > Passwords > Reset Master Password

### Credentials Visible in Plain Text

**Problem**: You see password in memory or logs

**Solution**:
1. This shouldn't happen - report as bug!
2. Immediately change the password
3. Regenerate SSH keys if exposed
4. Search logs for credential leaks
5. Report security issue confidentially

---

## Plugin Development Issues

### Changes Not Reflected After Rebuild

**Problem**: Modified code isn't loaded when running `./gradlew runIde`

**Solution**:
1. Clean and rebuild:
   ```bash
   ./gradlew clean runIde
   ```
2. Kill existing IDE processes:
   ```bash
   killall java
   ```
3. Clear sandbox directory:
   ```bash
   rm -rf ~/.IntelliJIdea*/sandbox
   ```
4. Rebuild plugin
5. Run: `./gradlew runIde`

### "Cannot find class" Errors

**Problem**: Kotlin class compilation fails

**Solution**:
1. Check package name matches file structure
2. Verify imports are correct
3. Check for circular dependencies
4. Run clean build: `./gradlew clean build`
5. Check IDE hasn't cached old bytecode

### Plugin Crashes on Launch

**Problem**: Exception thrown when plugin loads

**Solution**:
1. Check `~/.gradle/daemon/*/` for crash logs
2. Enable IDE debug mode: `./gradlew runIde --debug`
3. Attach debugger and set breakpoints
4. Check plugin.xml for invalid registrations
5. Verify all dependencies are available

---

## Performance Tuning

### Gradle Build Too Slow

```bash
# Increase memory
export GRADLE_OPTS="-Xmx2g"

# Use parallel building
./gradlew build --parallel

# Skip tests
./gradlew build -x test

# Use gradle daemon
./gradlew --daemon build
```

### IDE Sandbox Performance

```bash
# Run IDE with specific VM options
IDEA_VM_OPTIONS=-Xmx2g ./gradlew runIde
```

---

## Getting More Help

1. **Check Logs**: Help > Show Log in Explorer
2. **Community**: Search IntelliJ Platform forums
3. **Documentation**: https://plugins.jetbrains.com/docs/intellij/
4. **GitHub Issues**: Create detailed issue report
5. **Stack Overflow**: Tag with `intellij-plugin`

---

## Report a Bug Effectively

When reporting issues:

1. **Reproduce steps** - Exact steps to reproduce
2. **Expected behavior** - What should happen
3. **Actual behavior** - What actually happened
4. **Environment**:
   - IDE version and OS
   - Plugin version
   - SSH server details (type, OS)
5. **Logs** - Attach relevant log entries
6. **Screenshots** - If UI-related

Example:
```
Steps to reproduce:
1. Go to Tools > SFTP Deployment
2. Click Add under SSH Servers
3. Enter "example.com" as host
4. Click Test Connection

Expected: "Connected successfully" message
Actual: Error message "Connection timeout"

Environment: IntelliJ 2024.1, macOS 13.5, Plugin 1.0.0
SSH: OpenSSH 8.6
```

---

Good luck! 🚀
