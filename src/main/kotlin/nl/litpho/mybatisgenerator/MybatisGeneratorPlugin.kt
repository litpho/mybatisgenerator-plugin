package nl.litpho.mybatisgenerator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import java.util.Properties

class MybatisGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<MybatisGeneratorExtension>("mybatisgenerator")
        val mybatisgeneratorRuntimeConfiguration = project.configurations.maybeCreate("mybatisgeneratorRuntime")

        project.tasks.register<MybatisGeneratorTask>("generate") {
            this.configFile = extension.configFile.asFile.orNull ?: throw IllegalStateException("configFile must be set")
            this.javaTargetDir = extension.javaTargetDir.asFile.orNull ?: throw IllegalStateException("javaTargetDir must be set")
            this.resourcesTargetDir = extension.resourcesTargetDir.asFile.orNull ?: throw IllegalStateException("resourcesTargetDir must be set")
            this.configuration = mybatisgeneratorRuntimeConfiguration
            this.properties = Properties().apply { putAll(extension.properties.getOrElse(emptyMap())) }
        }
    }
}
