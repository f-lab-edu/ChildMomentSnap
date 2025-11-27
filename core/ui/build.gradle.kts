plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jg.childmomentsnap.core.ui"
    resourcePrefix = "core_ui_"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.navigationSuite)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.util)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}