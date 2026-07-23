# SFTP Deployment Plugin - Quick Start Guide

## Installation

1. **Download the Plugin**
   - Build from source: `./gradlew build`
   - Or download from JetBrains Marketplace (coming soon)

2. **Install in IntelliJ/PyCharm**
   - Go to `File > Settings > Plugins > ⚙️ > Install Plugin from Disk`
   - Select the `.jar` file
   - Restart IntelliJ

3. **Verify Installation**
   - Check `Tools > SFTP Deployment` appears in menu
   - You should see the settings page

## 5-Minute Setup

### Step 1: Create Your First SSH Server (2 minutes)

1. Go to `Tools > SFTP Deployment`
2. Click **Add** under "SSH Servers"
3. Fill in:
   - **Server Name**: `My Remote Server`
   - **Host**: `example.com`
   - **Port**: `22`
   - **User**: `deploy`
   - **Auth Type**: `PASSWORD` (or `KEY_PAIR`)
   - **Password**: Your SSH password
4. Click **Test Connection** - should show "Connected successfully"
5. Click **Save**

### Step 2: Create a Deployment Profile (2 minutes)

1. Click **Add** under "Deployment Profiles"
2. Fill in:
   - **Profile Name**: `Production`
   - **SSH Server**: Select "My Remote Server"
   - **Remote Path**: `/var/www/myapp`
   - **Auto-upload on Save**: Check this (optional)
3. Click **Save**

### Step 3: Start Using It (1 minute)

1. Right-click any file in your Project View
2. Select "Upload to Remote" → file is uploaded!
3. Or select "Download from Remote" → file is downloaded!

## Common Workflows

### Workflow 1: One-Time Upload
```
Right-click file → Upload to Remote
→ File appears on server at [Remote Path]/[Filename]
```

### Workflow 2: Auto-Upload Loop
```
1. Enable "Auto-upload on Save" in your profile
2. Edit file locally
3. Press Ctrl+S to save
4. File automatically uploads to server
5. Repeat for each change
```

### Workflow 3: Multiple Servers
```
1. Create multiple SSH servers with different hosts
2. Create multiple profiles pointing to different servers
3. Right-click file → select menu → choose profile
4. Upload to that specific server
```

### Workflow 4: Team Collaboration
```
1. Create profile: "Staging" (uploads to staging.example.com)
2. Create profile: "Production" (uploads to prod.example.com)
3. Test changes on Staging first
4. Upload to Production when ready
```

## Using SSH Key Authentication

If you prefer key-based authentication:

1. **Have your SSH key ready**
   - Usually at `~/.ssh/id_rsa`
   - Make sure permissions are correct: `chmod 600 ~/.ssh/id_rsa`

2. **Create SSH Server with Key Auth**
   - Server Name: `My Server`
   - Host: `example.com`
   - User: `deploy`
   - Auth Type: `KEY_PAIR`
   - Private Key Path: `/Users/myuser/.ssh/id_rsa`
   - Password/Passphrase: Leave empty if no passphrase, or enter if encrypted key

3. **Test Connection**
   - Should work without entering password

## Troubleshooting

### Connection Test Fails

**Error: "Connection refused"**
- Check hostname/IP is correct
- Verify SSH port (usually 22)
- Ensure server is reachable: `ping example.com`

**Error: "Authentication failed"**
- Check username is correct
- Verify password/key is correct
- For key auth: ensure file path is correct
- Check key has read permissions: `chmod 600 ~/.ssh/id_rsa`

**Error: "Network timeout"**
- Check SSH port isn't blocked by firewall
- Try connecting manually: `ssh user@host`

### Upload/Download Fails

**Error: "Remote path does not exist"**
- Create the directory on server: `mkdir -p /path/to/directory`
- Verify account has write permissions

**Error: "Permission denied"**
- Check file/directory permissions on server
- Ensure user has write access: `chmod 755 /var/www/myapp`

**Error: "File not found"**
- For upload: verify local file exists
- For download: verify remote file exists

### Auto-Upload Not Working

1. Check "Auto-upload on Save" is enabled in profile
2. Save a file with Ctrl+S (should upload silently)
3. Check server to confirm file appeared
4. If still not working, review Editor logs: `Help > Show Log in Explorer`

## Security Notes

### Password Storage
- Passwords are stored securely using your OS keychain
- Never shown in plain text or stored in project files
- Safe to commit your project to Git

### SSH Keys
- Private key file path is stored in project settings
- Key password/passphrase is stored in keychain
- Keep your private key file safe and backed up

### Best Practices
1. Use SSH keys instead of passwords when possible
2. Disable StrictHostKeyChecking only for trusted networks
3. Use separate credentials for dev, staging, and production
4. Regularly rotate credentials
5. Use firewall rules to restrict SSH access

## Advanced Features

### Using Multiple Profiles

You can have multiple SSH servers and deployment profiles:

```
Servers:
├── dev.example.com (dev-server)
├── staging.example.com (staging-server)
└── prod.example.com (prod-server)

Profiles:
├── Development → dev-server → /var/www/dev
├── Staging → staging-server → /var/www/app
└── Production → prod-server → /var/www/app
```

Switch between profiles by editing the deployment settings.

### Auto-Upload with Care

Auto-upload automatically uploads to the remote server on every save. 

**When to use:**
- Development environment with automatic reloading
- Quick iterations with testing

**When NOT to use:**
- Production deployments (use manual upload)
- Large files (bandwidth concerns)
- Unreliable connections

**Tip**: Enable for dev profile, disable for production profile

## Tips & Tricks

### 1. Test Before Uploading
Always click "Test Connection" after configuring a new server to verify connectivity works.

### 2. Use Meaningful Names
Name your servers and profiles clearly:
- ❌ "server1", "profile2"
- ✅ "AWS-PROD-1", "Production-Deployment"

### 3. Keep Remote Paths Consistent
Use the same remote base path across profiles:
- `example.com:/app/dev`
- `example.com:/app/staging`
- `example.com:/app/prod`

### 4. Backup Before Auto-Upload
If enabling auto-upload on production, ensure server-side backups are enabled.

### 5. Monitor Network Activity
For large deployments, watch your network usage. Upload/download is real-time SFTP.

## Need Help?

- **Settings Page Not Appearing**: Restart IntelliJ after installation
- **Plugin Not Loading**: Check `Help > About > Plugins` for errors
- **Need More Details**: Read [ARCHITECTURE.md](ARCHITECTURE.md) for technical details
- **Want to Contribute**: See [CONTRIBUTING.md](CONTRIBUTING.md)

## Next Steps

1. ✅ Install the plugin
2. ✅ Create an SSH server
3. ✅ Create a deployment profile
4. ✅ Test connection
5. ✅ Upload a test file
6. ✅ Celebrate! 🎉

Enjoy seamless SFTP deployment from your IDE!
