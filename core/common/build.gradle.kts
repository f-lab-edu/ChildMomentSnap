plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.android.library.compose)
}

android {
    namespace = "com.jg.childmomentsnap.core.common"

    buildTypes {
        release {
            isMinifyEnabled = false

        }
    }
}

dependencies {
    api(projects.core.model)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}