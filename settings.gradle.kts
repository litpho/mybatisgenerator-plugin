pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }

    repositories {
		mavenCentral()
		gradlePluginPortal()
    }
}

rootProject.name = "mybatisgenerator-plugin"
