pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "void-apps"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Core modules
include(":core:common")
include(":core:designsystem")
include(":core:licenses")
include(":core:testing")

// Template: folder apps/_template, project :apps:template (Gradle names cannot start with "_").
include(":apps:template")
project(":apps:template").projectDir = file("apps/_template")

// Apps — one line per app, alphabetical
