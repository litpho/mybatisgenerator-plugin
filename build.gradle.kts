plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.diffplug.spotless") version ("6.11.0")
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val junitVersion = "5.9.0"

dependencies {
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.1")

    compileOnly("org.liquibase:liquibase-core:4.17.0")
    // Temporary explicit commons-text until liquibase releases a fixed version
    compileOnly("org.apache.commons:commons-text:1.10.0")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.1")
    // keep on 1.12.4 until Gradle supports Kotlin 1.7
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

gradlePlugin {
    @Suppress("UNUSED_VARIABLE")
    val mybatis by plugins.creating {
        id = "nl.litpho.mybatisgenerator"
        implementationClass = "nl.litpho.mybatisgenerator.MybatisGeneratorPlugin"
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