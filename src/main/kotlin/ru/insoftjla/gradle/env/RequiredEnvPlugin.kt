package ru.insoftjla.gradle.env

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ProjectComponentIdentifier


class RequiredEnvPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val collectTask = target.tasks.register(
            "collectRequiredEnv",
            CollectRequiredEnvTask::class.java
        ) { task ->
            task.group = "verification"
            task.description = "Collects required environment variables"

            val runtimeClasspath = target.configurations
                .getByName("runtimeClasspath")

            val configFilesProvider = target.provider {
                val projectPaths = runtimeClasspath
                    .incoming
                    .resolutionResult
                    .allComponents
                    .mapNotNull { component ->
                        (component.id as? ProjectComponentIdentifier)
                            ?.projectPath
                    }
                    .toSet() + target.path

                val files = projectPaths
                    .mapNotNull { path ->
                        target.rootProject.findProject(path)
                    }
                    .flatMap { dependencyProject ->
                        target.fileTree(
                            dependencyProject.file("src/main/resources")
                        ) {
                            it.include("*.yml")
                            it.include("*.yaml")
                            it.include("*.properties")
                        }.files
                    }

                target.files(files)
            }

            task.inputFiles.from(configFilesProvider)

            task.outputFile.set(
                target.layout.buildDirectory.file("required-env.json")
            )
        }

        target.tasks.matching {
            it.name == "check"
        }.configureEach {
            it.dependsOn(collectTask)
        }
    }
}