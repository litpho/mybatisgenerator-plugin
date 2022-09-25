plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val junitVersion = "5.9.0"

dependencies {
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.1")

    compileOnly("org.liquibase:liquibase-core:4.16.1")
    // Temporary explicit snakeyaml until liquibase releases a fixed version
    compileOnly("org.yaml:snakeyaml:1.32")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.4.2")
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
