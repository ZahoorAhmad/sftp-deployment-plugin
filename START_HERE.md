# 🚀 START HERE - SFTP Deployment Plugin

Welcome to the **SFTP Deployment Plugin for PyCharm/IntelliJ Community Edition**!

This is your complete guide to understanding, building, and using this professional-grade plugin.

---

## ⚡ Quick Start (2 minutes)

### 1. Build the Plugin
```bash
cd /path/to/sftp-deployment-plugin
./gradlew build
```

### 2. Install in IntelliJ
- File > Settings > Plugins > ⚙️ > Install Plugin from Disk
- Select: `build/distributions/sftp-deployment-plugin-1.0.0.jar`
- Restart IntelliJ

### 3. Start Using
- Go to Tools > SFTP Deployment
- Add an SSH Server (click Add)
- Create a Deployment Profile
- Right-click any file > Upload to Remote

**Done!** ✨

---

## 📚 Documentation Guide

Choose what you need:

### For Users
| Document | What's Included |
|----------|-----------------|
| [QUICKSTART.md](QUICKSTART.md) | 5-minute setup, workflows, tips |
| [README.md](README.md) | Features, installation, usage |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | Common problems & solutions |

### For Developers
| Document | What's Included |
|----------|-----------------|
| [ARCHITECTURE.md](ARCHITECTURE.md) | Technical design, data flow, patterns |
| [BUILDING.md](BUILDING.md) | Build commands, development workflow |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Code style, testing, Git workflow |

### For Project Overview
| Document | What's Included |
|----------|-----------------|
| [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) | Complete overview & all features |
| [FILE_INVENTORY.md](FILE_INVENTORY.md) | What's included, statistics |

---

## 📁 What's Inside

### 7 Kotlin Source Files (~1,200 lines)
```
✓ Models.kt              - Data structures
✓ SecureCredentialManager.kt  - Secure storage
✓ SftpSettings.kt        - Plugin settings
✓ SftpClient.kt          - SFTP operations
✓ SftpSettingsPanel.kt   - Settings UI
✓ SftpSettingsConfigurable.kt - UI integration
✓ Actions.kt             - Upload/Download actions
```

### 8 Documentation Files (~17,000 words)
Comprehensive guides + troubleshooting

### Complete Gradle Setup
Ready to build immediately

### Production Ready
- Secure credential storage
- Error handling
- Background operations
- Modern UI

---

## 🎯 Features

✅ **SSH Servers** - Define multiple SSH servers  
✅ **Deployment Profiles** - Multiple deployment targets  
✅ **Authentication** - Password + SSH key support  
✅ **Secure Storage** - OS keychain integration  
✅ **Upload/Download** - Via context menu  
✅ **Auto-Upload** - Upload on Ctrl+S / Cmd+S  
✅ **Test Connection** - Verify connectivity  
✅ **Modern UI** - Native IntelliJ look & feel  

---

## 🛠️ Common Tasks

### I want to...

**...just use the plugin**
→ Read [QUICKSTART.md](QUICKSTART.md)

**...build the plugin**
→ Run `./gradlew build`

**...develop/extend it**
→ Read [BUILDING.md](BUILDING.md) then [ARCHITECTURE.md](ARCHITECTURE.md)

**...fix a problem**
→ Check [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

**...contribute code**
→ See [CONTRIBUTING.md](CONTRIBUTING.md)

**...understand the architecture**
→ Read [ARCHITECTURE.md](ARCHITECTURE.md)

---

## ❓ FAQ

**Q: Do I need IntelliJ Professional?**  
A: No! This plugin works with Community Edition. That's the whole point! ✨

**Q: Is it secure?**  
A: Yes! Passwords are stored in your OS keychain, never on disk.

**Q: Can I use SSH keys?**  
A: Yes! Both password and SSH key authentication supported.

**Q: Can I upload multiple files?**  
A: Currently single file operations. See [ARCHITECTURE.md](ARCHITECTURE.md) for future enhancements.

**Q: Does it work on Windows/Mac/Linux?**  
A: Yes to all! It's a pure Java/Kotlin plugin.

**Q: Is it open source?**  
A: Yes! MIT license. Contribute if you'd like.

---

## 📦 File Structure

```
sftp-deployment-plugin/
├── src/main/kotlin/com/wanclouds/sftpdeployment/
│   ├── model/              ← Data classes
│   ├── settings/           ← Plugin settings
│   ├── sftp/              ← SFTP operations
│   ├── ui/                ← Settings UI
│   └── actions/           ← Menu actions
├── src/main/resources/
│   ├── META-INF/plugin.xml  ← Plugin manifest
│   └── messages/            ← UI strings
├── build.gradle.kts         ← Build config
├── Documentation (8 files)
└── Gradle scripts
```

---

## 🔐 Security

- ✅ Passwords stored in OS keychain, never on disk
- ✅ No plain-text files in project
- ✅ Credentials cleared from memory
- ✅ SSH key paths configurable
- ✅ Secure credential rotation

---

## 🚀 Getting Started

### Step 1: Explore
```bash
# Look at what you have
ls -la
cat README.md          # Start here
cat PROJECT_SUMMARY.md # Full overview
```

### Step 2: Build
```bash
./gradlew build
# Creates: build/distributions/sftp-deployment-plugin-1.0.0.jar
```

### Step 3: Install & Test
```bash
# In IntelliJ:
# File > Settings > Plugins > Install Plugin from Disk > select JAR
# Tools > SFTP Deployment (should appear)
```

### Step 4: Use or Develop
- **User**: Follow [QUICKSTART.md](QUICKSTART.md)
- **Developer**: Follow [BUILDING.md](BUILDING.md)

---

## 💡 Pro Tips

1. **Test connection first** - Always click "Test Connection" before using
2. **Use SSH keys** - More secure than passwords
3. **Separate profiles** - Different profiles for dev/staging/prod
4. **Auto-upload with care** - Enable only for dev environments
5. **Back up remote** - Ensure server-side backups are enabled

---

## 🤔 Need Help?

1. **Installation problem?** → [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
2. **Want to use it?** → [QUICKSTART.md](QUICKSTART.md)
3. **Want to develop?** → [ARCHITECTURE.md](ARCHITECTURE.md)
4. **Found a bug?** → See CONTRIBUTING.md for bug reporting
5. **Want to contribute?** → [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 📞 Quick Reference

| What | Command |
|------|---------|
| Build | `./gradlew build` |
| Test in IDE | `./gradlew runIde` |
| Clean | `./gradlew clean` |
| View logs | Help > Show Log in Explorer |
| Install | Settings > Plugins > Install from Disk |

---

## ✨ What Makes This Special

This isn't just a plugin template. It's a **complete, production-ready solution** that:

✅ Works immediately (no extra setup)  
✅ Is secure by design (OS keychain integration)  
✅ Has professional UI (Kotlin UI DSL)  
✅ Handles errors gracefully  
✅ Includes comprehensive documentation  
✅ Is maintainable and extensible  
✅ Follows IntelliJ Platform best practices  

---

## 🎓 Learning Path

### For End Users
1. Read [QUICKSTART.md](QUICKSTART.md)
2. Set up your first server
3. Test connection
4. Upload a file
5. Enable auto-upload (optional)

### For Developers
1. Read [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)
2. Review [ARCHITECTURE.md](ARCHITECTURE.md)
3. Explore source code in `src/main/kotlin/`
4. Run `./gradlew runIde` to test
5. Modify and rebuild

---

## 🎉 Ready?

**Let's go!**

```bash
# Option 1: Just use it
cat QUICKSTART.md     # Read setup guide

# Option 2: Build it
./gradlew build       # Build the plugin

# Option 3: Develop it
./gradlew runIde      # Run in sandbox IDE
```

---

## 📄 License

MIT License - Use, modify, distribute freely!

---

## 🙏 Thank You!

Thank you for using/developing this plugin. We hope it saves you time and makes remote deployment easier!

**Happy deploying!** 🚀

---

**Last Updated:** July 2024  
**Plugin Version:** 1.0.0  
**Target:** PyCharm/IntelliJ Community Edition 2024.1+  

For more details, see [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)
