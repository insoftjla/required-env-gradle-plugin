# Required ENV Gradle Plugin

A Gradle plugin that collects required environment-variable placeholders from Spring configuration files and generates a JSON manifest. The manifest can be used in CI/CD pipelines to validate configuration before deployment.

## Features

- Scans Spring `application*.yml`, `application*.yaml`, and `application*.properties` files.
- Includes the project where the plugin is applied and its transitive Gradle project dependencies from `runtimeClasspath`.
- Collects placeholders without a default value, such as `${DATABASE_URL}`.
- Ignores placeholders with a default value, including an empty default (`${PORT:8080}` and `${OPTIONAL_VALUE:}`).
- Removes duplicates and produces a JSON file.
- Registers the `collectRequiredEnv` task and attaches it to the project's `check` task.

## Requirements

- A Gradle project with a `runtimeClasspath` configuration (for example, a Java or Spring Boot application).
- Java 17 or later to run the plugin.

## Installation

Apply the plugin to the application module. For a published release, Gradle Plugin Portal must be available in your plugin repositories.

**Groovy DSL** (`app/build.gradle`):

```groovy
plugins {
    id 'io.github.insoftjla.required-env' version '0.1.0'
}
```

**Kotlin DSL** (`app/build.gradle.kts`):

```kotlin
plugins {
    id("io.github.insoftjla.required-env") version "0.1.0"
}
```

If your build explicitly configures plugin repositories, include the Gradle Plugin Portal in `settings.gradle`:

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
```

> Version `0.1.0` must be published to the Gradle Plugin Portal before it can be resolved from there. For local development, use `publishToMavenLocal` and add `mavenLocal()` to `pluginManagement.repositories`.

## Usage

Run the task directly:

```bash
./gradlew :app:collectRequiredEnv
```

The task is also configured to run when `:app:check` executes, which is normally part of `:app:build` in Java-based projects:

```bash
./gradlew :app:build
```

The output is written to:

```text
app/build/required-env.json
```

### Example

`app/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSWORD}

service:
  timeout: ${REQUEST_TIMEOUT:5000}
  optional-token: ${OPTIONAL_TOKEN:}
```

Generated `app/build/required-env.json`:

```json
{
  "required": [
    "DATABASE_PASSWORD",
    "DATABASE_URL",
    "DATABASE_USER"
  ]
}
```

A placeholder is considered required when it has **no** `:` default-value separator. Both `${NAME:default}` and `${NAME:}` are excluded from the required list.

## Multi-module projects

When applied to `:app`, the plugin uses the resolved `runtimeClasspath` dependency graph to identify the application module and transitive **project** dependencies. It scans Spring configuration files under each selected module's `src/main/resources` directory.

For example, if `:app` depends on `:core`, and `:core` depends on `:finance`, files in all three modules are considered. An unrelated module is not scanned.

External Maven dependency JARs are not scanned.

## CI/CD example

```yaml
collect-required-env:
  stage: validate
  script:
    - ./gradlew :app:collectRequiredEnv
  artifacts:
    paths:
      - app/build/required-env.json
```

The generated manifest contains variable names, **not values or secrets**. A separate CI job can read the manifest and verify that required keys exist in a secret store such as HashiCorp Vault.

## Current limitations

- Only Spring configuration files matching `*.yml`, `*.yaml`, and `*.properties` under `src/main/resources` are scanned.
- Java/Kotlin calls such as `System.getenv(...)`, arbitrary YAML files, and external configuration imports are not scanned.
- The current scanner uses a regular expression and does not fully parse nested Spring placeholders (for example, `${PRIMARY:${FALLBACK}}`).
- A placeholder without a default value is treated as required regardless of which Spring profile contains it. There is no profile-specific output yet.
- Dependency discovery resolves `runtimeClasspath`, so it may need access to configured dependency repositories.
- Configuration Cache and Isolated Projects compatibility have not yet been established.

## Development

Build and test the plugin:

```bash
./gradlew build
```

Publish it to the local Maven repository for testing:

```bash
./gradlew publishToMavenLocal
```

In the consuming project's `settings.gradle`, add `mavenLocal()` under `pluginManagement.repositories`, and apply the plugin with the same ID and version shown above.

## Source code

[GitHub repository](https://github.com/insoftjla/required-env-gradle-plugin)
