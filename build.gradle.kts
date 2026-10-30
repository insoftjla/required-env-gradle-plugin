import org.gradle.plugin.compatibility.compatibility

plugins {
    id("com.gradle.plugin-publish") version "2.2.1"
    kotlin("jvm") version "2.3.20"
}

group = "ru.insoftjla.gradle"
version = "0.1.0"

repositories {
    mavenCentral()
}

gradlePlugin {
    website = "https://github.com/insoftjla/required-env-gradle-plugin"
    vcsUrl = "https://github.com/insoftjla/required-env-gradle-plugin"

    plugins {
        create("requiredEnv") {
            id = "ru.insoftjla.gradle.required-env"
            implementationClass = "ru.insoftjla.gradle.env.RequiredEnvPlugin"

            displayName = "Required ENV Gradle Plugin"
            description =
                "Collects required environment variables from Spring configuration files in Gradle project dependencies"

            tags = listOf(
                "environment",
                "spring",
                "configuration",
                "validation"
            )

            compatibility {
                features {
                    configurationCache = false
                    isolatedProjects = false
                }
            }
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}