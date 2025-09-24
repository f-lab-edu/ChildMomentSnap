plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.jg.childmomentsnap.core.data"
}

dependencies {
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    
}