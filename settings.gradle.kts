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
        google()
        mavenCentral()
    }
}

rootProject.name = "ChildMomentSnap"
include(":app")
include(":core")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:model")
include(":feature")
include(":feature:calendar")
include(":core:network")
include(":feature:diary")
include(":feature:dairy-detail")
include(":feature:photo")
include(":feature:voice")
