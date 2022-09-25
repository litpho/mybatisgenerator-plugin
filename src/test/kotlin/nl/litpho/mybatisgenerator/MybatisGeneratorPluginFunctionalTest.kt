package nl.litpho.mybatisgenerator

import io.kotest.matchers.should
import io.kotest.matchers.string.contain
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File

class MybatisGeneratorPluginFunctionalTest {

    @TempDir(cleanup = CleanupMode.NEVER)
    private lateinit var tempDir: File

    @BeforeEach
    internal fun setUp() {
        tempDir.mkdirs()
    }

    private fun getBuildFile(): File = tempDir.resolve("build.gradle")
    private fun getSettingsFile(): File = tempDir.resolve("settings.gradle")
    private fun getConfigFile(): File = tempDir.resolve("generatorConfig.xml")
    private fun getChangelogFile(): File = tempDir.resolve("changelog-master.xml")

    @Test
    fun `can run task`() {
        getSettingsFile().writeText("")
        val generatorConfigUrl = MybatisGeneratorPluginFunctionalTest::class.java.getResource("/nl/litpho/mybatisgenerator/generatorConfig.xml")!!
        File(generatorConfigUrl.file).copyTo(getConfigFile())
        val changelogMasterUrl = MybatisGeneratorPluginFunctionalTest::class.java.getResource("/nl/litpho/mybatisgenerator/changelog-master.xml")!!
        File(changelogMasterUrl.file).copyTo(getChangelogFile())
        getBuildFile().writeText("""
plugins {
    id('nl.litpho.mybatisgenerator')
}

repositories {
    mavenCentral()
}

mybatisgenerator {
    configFile = file("generatorConfig.xml")
    directories {
        java = file("${'$'}{project.buildDir}/generatedSources/src/main/java")
        resources = file("${'$'}{project.buildDir}/generatedSources/src/main/resources")
    }
    database {
        connectionUrl = "jdbc:h2:${'$'}{projectDir}/build/db/h2"
        driverClass = "org.h2.Driver"
        username = "sa"
        password = ""
    }
    liquibase {
        changelogLocation = file("changelog-master.xml")
    }
}

dependencies {
    mybatisgeneratorRuntime 'com.h2database:h2:2.1.214'
}
""")

        val result = GradleRunner.create().apply {
            forwardOutput()
            withPluginClasspath()
            withArguments("generate", "--info")
            withProjectDir(tempDir)
        }.build()

        result.output should contain("did not resolve to any tables")
    }
}
