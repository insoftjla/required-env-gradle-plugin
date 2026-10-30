plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "2.3.20"
}

group = "ru.insoftjla.gradle"
version = "0.1.0"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("requiredEnv") {
            id = "ru.insoftjla.gradle.required-env"
            implementationClass = "ru.insoftjla.gradle.env.RequiredEnvPlugin"
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