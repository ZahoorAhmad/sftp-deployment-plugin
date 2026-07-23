plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.wanclouds.sftpdeployment"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.mwiede:jsch:0.2.16")
}

intellij {
    version.set("2023.2.4")
    type.set("IC")
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set(provider { null })
    }
    buildPlugin {
        archiveBaseName.set("my-custom-sftp-plugin")
    }
}
