plugins {
    alias(libs.plugins.cms.android.library.compose)
    alias(libs.plugins.cms.android.feature)
}


android {
    namespace = "com.jg.childmomentsnap.feature.calendar"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}