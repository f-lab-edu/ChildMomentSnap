plugins {
    alias(libs.plugins.cms.android.library.compose)
    alias(libs.plugins.cms.android.feature)
}


android {
    namespace = "com.jg.childmomentsnap.feature.calendar"

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}