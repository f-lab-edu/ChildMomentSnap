plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.hilt)
}

android {
    namespace = "com.jg.childmomentsnap.core.common.android"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
