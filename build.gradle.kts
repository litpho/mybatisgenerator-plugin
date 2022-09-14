plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

val junitVersion = "5.9.0"

dependencies {
    implementation("org.liquibase:liquibase-core:4.16.0")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.1")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.4.2")
    testImplementation("io.mockk:mockk:1.12.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

gradlePlugin {
    val mybatis by plugins.creating {
        id = "nl.litpho.mybatisgenerator"
        implementationClass = "nl.litpho.mybatisgenerator.MybatisGeneratorPlugin"
    }
}

tasks.withType<Test>() {
    useJUnitPlatform()
}
