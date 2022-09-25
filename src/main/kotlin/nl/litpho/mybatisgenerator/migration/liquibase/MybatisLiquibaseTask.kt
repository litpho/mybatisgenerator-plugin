package nl.litpho.mybatisgenerator.migration.liquibase

import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import java.io.File

open class MybatisLiquibaseTask : JavaExec() {

    @Input
    val connectionUrl: Property<String> = project.objects.property()

    @Input
    val driverClass: Property<String> = project.objects.property()

    @Input
    val username: Property<String> = project.objects.property()

    @Input
    val password: Property<String> = project.objects.property()

    @Input
    val changelogLocation: RegularFileProperty = project.objects.fileProperty()

    @Input
    val contexts: ListProperty<String> = project.objects.listProperty()

    @Internal
    lateinit var configuration: Configuration

    @TaskAction
    override fun exec() {
        val connectionUrl = requireNotNull(connectionUrl.orNull) { "database.connectionUrl is required" }
        val driverClass = requireNotNull(driverClass.orNull) { "database.driverClass is required" }
        val username = requireNotNull(username.orNull) { "database.username is required" }
        val password = requireNotNull(password.orNull) { "database.password is required" }
        val changelogLocation: File =
            requireNotNull(changelogLocation.asFile.orNull) { "liquibase.changelogLocation is required" }
        val contexts: List<String> = requireNotNull(contexts.orNull) { "liquibase.contexts is required" }

        classpath = project.objects.fileCollection().from(configuration.resolvedConfiguration.files)
        args = listOf(
            "--url=$connectionUrl",
            "--driver=$driverClass",
            "--username=$username",
            "--password=$password",
            "--search-path=${changelogLocation.parent}",
            "--changelog-file=${changelogLocation.name}",
            "--contexts=${contexts.joinToString(",")}",
            "update"
        )

        super.exec()
    }
}