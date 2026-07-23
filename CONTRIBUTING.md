# Contributing to SFTP Deployment Plugin

Thank you for your interest in contributing! This document provides guidelines for contributing to the project.

## Code of Conduct

- Be respectful and constructive
- Help others learn and grow
- Report issues professionally

## How to Contribute

### 1. Fork and Clone
```bash
git clone https://github.com/yourusername/sftp-deployment-plugin.git
cd sftp-deployment-plugin
```

### 2. Create a Feature Branch
```bash
git checkout -b feature/your-feature-name
```

### 3. Make Your Changes

Follow these guidelines:
- Use Kotlin idioms and best practices
- Follow the existing code style (2-space indentation)
- Add comments for complex logic
- Keep functions focused and small
- Use descriptive variable and function names

### 4. Test Your Changes

```bash
# Build the plugin
./gradlew build

# Run in sandbox IDE
./gradlew runIde

# Test in the sandbox:
# - Settings UI changes
# - Connection testing
# - File upload/download
# - Auto-upload functionality
```

### 5. Commit with Clear Messages

```bash
git commit -m "feat: Add feature name

- Brief description of change
- Implementation details if needed
- Related issue number (#123)
"
```

### 6. Push and Create Pull Request

```bash
git push origin feature/your-feature-name
```

Then create a Pull Request with:
- Clear title and description
- Reference to related issues
- Screenshots for UI changes
- Testing notes

## Development Guidelines

### Code Style

```kotlin
// Good: Clear, concise, idiomatic Kotlin
fun uploadFile(server: SshServer, localPath: String) {
    val result = SftpClient.upload(server, localPath, server.remotePath)
    when (result) {
        is Result.Success -> showNotification("Success: ${result.data}")
        is Result.Error -> showNotification("Error: ${result.message}")
    }
}

// Bad: Verbose, mixed styles
fun uploadFile(server: SshServer, localPath: String) {
    try {
        val sftp = SftpClient()
        val res = sftp.upload(server, localPath, server.remotePath)
        if (res != null) {
            println(res)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

### Working with IntelliJ APIs

- Keep dependencies minimal - use platform APIs when available
- Use `@Transient` annotations for non-persistent fields
- Implement proper `@State` annotations for persistent components
- Use `ApplicationManager.getApplication().invokeLater()` for UI updates
- Use `ProgressManager` for background tasks

### Testing

Before submitting:
1. Test all modified features
2. Verify no regressions in existing features
3. Test in both PyCharm and IntelliJ Community
4. Test with different authentication methods (password and key)
5. Test auto-upload with multiple profiles

### Documentation

- Update README.md for user-facing changes
- Add JavaDoc/KDoc for public APIs
- Update BUILDING.md if build process changes
- Include comments for non-obvious logic

## Areas for Contribution

### Easy (Good for First Timers)
- [ ] Improve error messages
- [ ] Add more resource strings to messages.properties
- [ ] Enhance documentation
- [ ] Fix typos

### Medium
- [ ] Add file type filtering (upload only .py files, etc.)
- [ ] Implement directory sync
- [ ] Add preview/diff before upload
- [ ] Improve notification grouping

### Advanced
- [ ] Add support for SFTP URL schemes
- [ ] Implement conflict resolution strategies
- [ ] Add version control integration
- [ ] Implement batch upload/download
- [ ] Add cloud provider support (AWS S3, Azure, etc.)

## Reporting Issues

When reporting bugs:
1. Use clear, descriptive titles
2. Provide step-by-step reproduction steps
3. Include error messages and stack traces
4. Specify IntelliJ version and OS
5. Attach relevant logs from `Help > Show Log in Explorer`

## Questions?

- Check existing issues and discussions
- Review IntelliJ Platform plugin development docs
- Ask in comments on relevant issue/PR

## Thank You!

Your contributions make this plugin better for everyone. Thank you for your time and effort!
