pluginManagement {
    repositories {
        includeBuild("build-logic")
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

rootProject.name = "ChildMomentSnap"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:model")
include(":core:domain")
include(":core:network")
include(":core:ui")
include(":core:common-android")
include(":feature:calendar")
include(":feature:diary")
include(":feature:moment")
include(":core:common")
include(":feature:home")
include(":feature:my")
