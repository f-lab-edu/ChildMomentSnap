plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.android.library.compose)
}

android {
    namespace = "com.jg.childmomentsnap.core.ui"
}

dependencies {
    api(projects.core.model)

    implementation(libs.coil.kt)
    implementation(libs.coil.compose)
}