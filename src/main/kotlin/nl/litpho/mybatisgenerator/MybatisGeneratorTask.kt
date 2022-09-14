package nl.litpho.mybatisgenerator

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.mybatis.generator.api.MyBatisGenerator
import org.mybatis.generator.api.VerboseProgressCallback
import org.mybatis.generator.config.Configuration
import org.mybatis.generator.config.xml.ConfigurationParser
import org.mybatis.generator.internal.DefaultShellCallback
import java.io.File
import java.util.*

open class MybatisGeneratorTask: DefaultTask() {

    @InputFile
    lateinit var configFile: File

    @OutputDirectory
    lateinit var javaTargetDir: File

    @OutputDirectory
    lateinit var resourcesTargetDir: File

    @Input
    lateinit var properties: Properties

    @Internal
    lateinit var configuration: org.gradle.api.artifacts.Configuration

    @TaskAction
    fun run() {
        javaTargetDir.mkdirs()
        resourcesTargetDir.mkdirs()
        properties["projectDir"] = project.projectDir.absolutePath
        properties["javaTargetDir"] = javaTargetDir.absolutePath
        properties["resourcesTargetDir"] = resourcesTargetDir.absolutePath

        val warnings: MutableList<String> = mutableListOf()
        val overwrite = true
        val cp = ConfigurationParser(properties, warnings)
        val config: Configuration = cp.parseConfiguration(configFile)
        configuration.resolvedConfiguration.files.forEach {
            config.addClasspathEntry(it.absolutePath)
        }
        val callback = DefaultShellCallback(overwrite)
        val myBatisGenerator = MyBatisGenerator(config, callback, warnings)
        val progressCallback = VerboseProgressCallback()
        myBatisGenerator.generate(progressCallback)

        warnings.forEach {
            project.logger.lifecycle(it)
        }
    }
}