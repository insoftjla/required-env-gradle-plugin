package ru.insoftjla.gradle.env

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

abstract class CollectRequiredEnvTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun collect() {
        val variables = inputFiles.files
            .asSequence()
            .flatMap { SpringEnvScanner.scan(it.readText()).asSequence() }
            .toSortedSet()

        val json = variables.joinToString(
            separator = ",\n",
            prefix = "{\n  \"required\": [\n",
            postfix = "\n  ]\n}"
        ) { "    \"$it\"" }

        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(json)

        logger.lifecycle(
            "Found ${variables.size} required ENV variables: ${outputFile.path}"
        )
    }

}