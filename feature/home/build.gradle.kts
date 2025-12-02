plugins {
    alias(libs.plugins.cms.android.library.compose)
    alias(libs.plugins.cms.android.feature)
}

android {
    namespace = "com.jg.childmomentsnap.home"
}

dependencies {
    implementation(projects.feature.moment)
    implementation(projects.feature.calendar)
    implementation(projects.feature.my)
    implementation(projects.feature.diary)

    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}