plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jg.childmomentsnap.core.data"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    api(projects.core.common)
    api(projects.core.domain)
    api(projects.core.model)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)
}