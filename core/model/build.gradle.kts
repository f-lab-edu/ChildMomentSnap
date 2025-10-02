plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jg.childmomentsnap.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    api(libs.kotlinx.datetime)
}