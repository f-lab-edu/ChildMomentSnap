plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.hilt)
}

android {
    namespace = "com.jg.childmomentsnap.core.datastore"
}

dependencies {
    api(libs.androidx.dataStore)
    api(projects.core.model)
}