fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.12.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
}

dependencies {
    implementation("org.bytedeco:ffmpeg:5.1.2-1.5.8")
    implementation("org.bytedeco:javacv:1.5.8")
    implementation("org.bytedeco:ffmpeg-platform:5.1.2-1.5.8")
}

// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin {
    jvmToolchain(17)
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion")
    }
    buildSearchableOptions {
        enabled = false
    }
}
