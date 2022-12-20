plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    signing
    id("com.diffplug.spotless") version ("6.11.0")
    id("com.gradle.plugin-publish") version "1.1.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val junitVersion = "5.9.1"

dependencies {
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.1")

    compileOnly("org.liquibase:liquibase-core:4.18.0")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.4")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

gradlePlugin {
    plugins.create("mybatisGeneratorPlugin") {
        id = "nl.litpho.mybatisgenerator"
        implementationClass = "nl.litpho.mybatisgenerator.MybatisGeneratorPlugin"
        displayName = "Gradle Mybatisgenerator with Liquibase plugin"
        description = "Gradle Mybatisgenerator plugin using Liquibase"
        vcsUrl.set("https://github.com/litpho/mybatisgenerator-plugin")
        website.set("https://github.com/litpho/mybatisgenerator-plugin")
        tags.set(listOf("mybatis", "mybatisgenerator", "liquibase", "plugin"))
    }
}

tasks.withType<Test>() {
    useJUnitPlatform()
}

spotless {
    kotlin {
        ktlint()
    }
}