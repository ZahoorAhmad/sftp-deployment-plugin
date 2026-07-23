# Building the SFTP Deployment Plugin

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Git
- IntelliJ IDEA (any edition for development)

## Build Instructions

### 1. Clone or navigate to the project
```bash
cd /path/to/sftp-deployment-plugin
```

### 2. Build with Gradle
```bash
# On macOS/Linux
./gradlew build

# On Windows
gradlew.bat build
```

The built plugin will be created at: `build/distributions/sftp-deployment-plugin-1.0.0.jar`

### 3. Build and Run in IDE
To test the plugin in a sandboxed IntelliJ instance:

```bash
# On macOS/Linux
./gradlew runIde

# On Windows
gradlew.bat runIde
```

This opens a new IntelliJ instance with the plugin loaded. You can then:
- Test the settings UI (Tools > SFTP Deployment)
- Test context menu actions
- Test all features

### 4. Install the Plugin Locally

**Option A: From the IDE**
1. Build the plugin: `./gradlew build`
2. In IntelliJ: `File > Settings > Plugins > ⚙️ > Install Plugin from Disk...`
3. Select the JAR file from `build/distributions/`
4. Restart IntelliJ

**Option B: Manual Installation**
1. Build: `./gradlew build`
2. Locate the JAR: `build/distributions/sftp-deployment-plugin-1.0.0.jar`
3. Copy to your plugins directory:
   - **Linux/Mac**: `~/.config/JetBrains/IntelliJ*/plugins/`
   - **Windows**: `%APPDATA%\JetBrains\IntelliJIdea*\plugins\`
4. Restart IntelliJ

## Development Workflow

### Debugging the Plugin

1. Run with debugging enabled:
```bash
./gradlew runIde --debug-jvm
```

2. In IntelliJ, attach a debugger (Run > Edit Configurations > Remote)

### Code Structure

```
src/main/kotlin/com/wanclouds/sftpdeployment/
├── actions/
│   └── Actions.kt              # Upload/Download actions and auto-upload listener
├── model/
│   ├── Models.kt               # Data classes and enums
│   └── SecureCredentialManager.kt  # Credential storage
├── settings/
│   └── SftpSettings.kt          # PersistentStateComponent
├── sftp/
│   └── SftpClient.kt            # JSch-based SFTP operations
└── ui/
    ├── SftpSettingsPanel.kt     # Master-Detail UI
    └── SftpSettingsConfigurable.kt  # SearchableConfigurable integration
```

### Making Changes

1. Edit the Kotlin files as needed
2. Run `./gradlew build` to verify compilation
3. Use `./gradlew runIde` to test in sandbox
4. Commit and push changes

## Common Build Tasks

```bash
# Clean build artifacts
./gradlew clean

# Build without running tests
./gradlew build -x test

# View all available tasks
./gradlew tasks

# Check for dependency updates
./gradlew dependencyUpdates

# Run with increased logging
./gradlew build --info
```

## Troubleshooting Build Issues

### "Cannot find JDK 17"
Ensure JDK 17+ is installed and JAVA_HOME is set:
```bash
export JAVA_HOME=/path/to/jdk17  # macOS/Linux
set JAVA_HOME=C:\path\to\jdk17   # Windows
```

### Gradle Wrapper Issues
Reset the wrapper:
```bash
./gradlew wrapper --gradle-version 8.5
```

### IDE Plugin Conflicts
If you get "Duplicate plugin ID", uninstall older versions from:
`Settings > Plugins > Search for "SFTP Deployment" > Uninstall`

## Publishing to JetBrains Marketplace

1. Create account at https://plugins.jetbrains.com/
2. Verify email
3. Create plugin page
4. Upload the JAR from `build/distributions/`
5. Wait for review (typically 24-48 hours)

## Performance Tips

- Run `./gradlew build` with `org.gradle.workers.max=4` for faster builds
- Use `--parallel` flag for multi-module builds
- Cache dependencies: `org.gradle.caching=true` in gradle.properties

## Support

For issues, refer to IntelliJ Platform Plugin Development documentation:
- https://plugins.jetbrains.com/docs/intellij/
- https://github.com/JetBrains/intellij-sdk-code-samples
