package nl.litpho.mybatisgenerator

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldNot
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class MybatisGeneratorPluginTest {

    @Test
    fun `plugin registers task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("nl.litpho.mybatisgenerator")

        project.tasks.findByName("generate") shouldNot beNull()
    }
}
