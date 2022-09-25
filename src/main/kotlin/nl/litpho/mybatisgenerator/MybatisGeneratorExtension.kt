package nl.litpho.mybatisgenerator

import org.gradle.api.Action
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class MybatisGeneratorExtension @Inject constructor(private val objects: ObjectFactory) {
    val configFile: RegularFileProperty = objects.fileProperty()
    val directoriesHandler: DirectoriesHandler = objects.newInstance()
    val databaseHandler: DatabaseHandler = objects.newInstance()
    lateinit var liquibaseHandler: LiquibaseHandler
    val properties: MutableMap<String, String> = mutableMapOf()

    fun addProperty(key: String, value: String) {
        properties[key] = value
    }

    fun directories(action: Action<DirectoriesHandler>) {
        action.execute(directoriesHandler)
    }

    fun database(action: Action<DatabaseHandler>) {
        action.execute(databaseHandler)
    }

    fun liquibase(action: Action<LiquibaseHandler>) {
        liquibaseHandler = objects.newInstance()
        action.execute(liquibaseHandler)
    }

    fun liquibaseInitialized(): Boolean = this::liquibaseHandler.isInitialized
}

open class DirectoriesHandler @Inject constructor(objects: ObjectFactory) {
    val java: RegularFileProperty = objects.fileProperty()
    val resources: RegularFileProperty = objects.fileProperty()
}

open class DatabaseHandler @Inject constructor(objects: ObjectFactory) {
    val connectionUrl: Property<String> = objects.property()
    val username: Property<String> = objects.property()
    val password: Property<String> = objects.property()
    val driverClass: Property<String> = objects.property()
}

open class LiquibaseHandler @Inject constructor(objects: ObjectFactory) {
    val changelogLocation: RegularFileProperty = objects.fileProperty()
    val contexts: ListProperty<String> = objects.listProperty<String>().convention(listOf("none"))
}