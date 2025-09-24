plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.hilt)
    id("kotlinx-serialization")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.jg.childmomentsnap.core.network"
}

dependencies {
    api(libs.kotlinx.datetime)
    api(projects.core.model)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.svg)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization)

}