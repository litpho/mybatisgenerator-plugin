package nl.litpho.mybatisgenerator

import nl.litpho.mybatisgenerator.migration.liquibase.MybatisLiquibaseTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import java.util.Properties

class MybatisGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<MybatisGeneratorExtension>("mybatisgenerator")
        val liquibaseRuntimeConfiguration = project.configurations.maybeCreate("liquibaseRuntime")
        val mybatisgeneratorRuntimeConfiguration = project.configurations.maybeCreate("mybatisgeneratorRuntime")

        project.tasks.register<MybatisGeneratorTask>("generate") {
            configFile.set(extension.configFile)
            javaTargetDir.set(extension.directoriesHandler.java)
            resourcesTargetDir.set(extension.directoriesHandler.resources)
            connectionUrl.set(extension.databaseHandler.connectionUrl)
            driverClass.set(extension.databaseHandler.driverClass)
            username.set(extension.databaseHandler.username)
            password.set(extension.databaseHandler.password)
            this.configuration = mybatisgeneratorRuntimeConfiguration
            properties = Properties().apply { putAll(extension.properties) }
        }

        project.afterEvaluate {
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val main = sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME)
            main.get().apply {
                java {
                    srcDirs.add(extension.directoriesHandler.java.asFile.get())
                }
                extension.directoriesHandler.resources.asFile.orNull?.let { resourcesDir ->
                    resources.srcDirs.add(resourcesDir)
                }
            }

            if (extension.liquibaseInitialized()) {
                project.tasks.register<MybatisLiquibaseTask>("generatorDatabaseUpdate") {
                    mainClass.set("liquibase.integration.commandline.LiquibaseCommandLine")
                    connectionUrl.set(extension.databaseHandler.connectionUrl)
                    driverClass.set(extension.databaseHandler.driverClass)
                    username.set(extension.databaseHandler.username)
                    password.set(extension.databaseHandler.password)
                    changelogLocation.set(extension.liquibaseHandler.changelogLocation)
                    logLevel.set(extension.liquibaseHandler.logLevel)
                    contexts.set(extension.liquibaseHandler.contexts)
                    this.configuration = liquibaseRuntimeConfiguration
                }

                project.tasks["generate"].dependsOn("generatorDatabaseUpdate")
            }

        }
    }
}
