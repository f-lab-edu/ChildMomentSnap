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
    api(projects.core.model)

    implementation(projects.core.domain)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.network)
}