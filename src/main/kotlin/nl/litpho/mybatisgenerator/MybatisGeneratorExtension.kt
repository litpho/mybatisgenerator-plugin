package nl.litpho.mybatisgenerator

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty

interface MybatisGeneratorExtension {
    val configFile: RegularFileProperty
    val javaTargetDir: RegularFileProperty
    val resourcesTargetDir: RegularFileProperty
    val properties: MapProperty<String, String>
}