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

    defaultConfig {
        buildConfigField(
            "String",
            "GOOGLE_VISION_BASE_URL",
            "\"https://vision.googleapis.com/\""
        )
        buildConfigField(
            "String",
            "GOOGLE_SPEECH_BASE_URL",
            "\"https://speech.googleapis.com/\""
        )
    }
}

dependencies {
    api(libs.kotlinx.datetime)
    api(projects.core.model)
    api(projects.core.common)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.google.generativeai)
}
