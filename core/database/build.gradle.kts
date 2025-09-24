plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.hilt)
    alias(libs.plugins.cms.android.room)
}

android {
    namespace = "com.jg.childmomentsnap.core.database"
}

dependencies {
    api(projects.core.model)

    implementation(libs.kotlinx.datetime)
}