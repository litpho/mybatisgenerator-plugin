package nl.litpho.mybatisgenerator

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import org.mybatis.generator.api.MyBatisGenerator
import org.mybatis.generator.api.VerboseProgressCallback
import org.mybatis.generator.config.Configuration
import org.mybatis.generator.config.xml.ConfigurationParser
import org.mybatis.generator.internal.DefaultShellCallback
import java.io.File
import java.util.Properties

open class MybatisGeneratorTask : DefaultTask() {

    @InputFile
    val configFile: RegularFileProperty = project.objects.fileProperty()

    @OutputDirectory
    val javaTargetDir: RegularFileProperty = project.objects.fileProperty()

    @Optional
    @OutputDirectory
    val resourcesTargetDir: RegularFileProperty = project.objects.fileProperty()

    @Internal
    val connectionUrl: Property<String> = project.objects.property()

    @Internal
    val driverClass: Property<String> = project.objects.property()

    @Internal
    val username: Property<String> = project.objects.property()

    @Internal
    val password: Property<String> = project.objects.property()

    @Input
    lateinit var properties: Properties

    @Internal
    lateinit var configuration: org.gradle.api.artifacts.Configuration

    init {
        group = "Mybatis Generator"
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun run() {
        val configFile = requireNotNull(configFile.asFile.orNull) { "configFile is required" }
        val javaTargetDir: File = requireNotNull(javaTargetDir.asFile.orNull) { "directories.java is required" }
        val resourcesTargetDir = resourcesTargetDir.asFile.orNull
        val connectionUrl = connectionUrl.orNull
        val driverClass = driverClass.orNull
        val username = username.orNull
        val password = password.orNull

        createTargetDirs(javaTargetDir, resourcesTargetDir)
        initializeDefaultProperties(javaTargetDir, resourcesTargetDir, connectionUrl, driverClass, username, password)

        val warnings: MutableList<String> = mutableListOf()
        val config: Configuration = createConfiguration(configFile, properties, warnings)
        val callback = DefaultShellCallback(true)

        MyBatisGenerator(config, callback, warnings).generate(VerboseProgressCallback())

        warnings.forEach {
            project.logger.lifecycle(it)
        }
    }

    private fun createConfiguration(
        configFile: File,
        properties: Properties,
        warnings: MutableList<String>
    ): Configuration =
        ConfigurationParser(properties, warnings)
            .parseConfiguration(configFile)
            .also { config ->
                configuration.resolvedConfiguration.files.forEach {
                    config.addClasspathEntry(it.absolutePath)
                    project.logger.info("Adding {} to the classpath", it.absolutePath)
                }
            }

    private fun createTargetDirs(javaTargetDir: File, resourcesTargetDir: File?) {
        javaTargetDir.mkdirs()
        resourcesTargetDir?.mkdirs()
    }

    private fun initializeDefaultProperties(
        javaTargetDir: File,
        resourcesTargetDir: File?,
        connectionUrl: String?,
        driverClass: String?,
        username: String?,
        password: String?
    ) {
        with(properties) {
            this["projectDir"] = project.projectDir.absolutePath
            this["javaTargetDir"] = javaTargetDir.absolutePath
            resourcesTargetDir?.let { this["resourcesTargetDir"] = resourcesTargetDir.absolutePath }
            connectionUrl?.let { this["connectionUrl"] = connectionUrl }
            driverClass?.let { this["driverClass"] = driverClass }
            username?.let { this["username"] = username }
            password?.let { this["password"] = password }
        }
    }
}