import java.util.Properties

plugins {
    alias(libs.plugins.cms.android.application)
    alias(libs.plugins.cms.android.application.compose)
    alias(libs.plugins.cms.hilt)
    alias(libs.plugins.kotlin.serialization)
}


android {
    buildFeatures {
        buildConfig = true
    }
    
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jg.childmomentsnap"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "GOOGLE_CLOUD_API_KEY",
                getLocalProperty("GOOGLE_CLOUD_API_KEY")
            )
            buildConfigField(
                "String",
                "GOOGLE_CLOUD_PROJECT_ID",
                getLocalProperty("GOOGLE_CLOUD_PROJECT_ID")
            )
            buildConfigField(
                "String",
                "GOOGLE_APPLICATION_CREDENTIALS",
                "\"google_credentials\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "GOOGLE_CLOUD_API_KEY",
                getLocalProperty("GOOGLE_CLOUD_API_KEY")
            )
            buildConfigField(
                "String",
                "GOOGLE_CLOUD_PROJECT_ID",
                getLocalProperty("GOOGLE_CLOUD_PROJECT_ID")
            )
            buildConfigField(
                "String",
                "GOOGLE_APPLICATION_CREDENTIALS",
                "\"google_credentials\""
            )
        }
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
        }
    }
    
    namespace = "com.jg.childmomentsnap.apps"
}

dependencies {

    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.ui)
    implementation(projects.core.common)

    implementation(projects.feature.calendar)
    implementation(projects.feature.diary)
    implementation(projects.feature.dairyDetail)
    implementation(projects.feature.photo)
    implementation(projects.feature.voice)


    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)

    ksp(libs.hilt.compiler)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlin.test)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //testImplementation(libs.androidx.navigation.testing)
}

fun getLocalProperty(key: String): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    return properties.getProperty(key) ?: ""
}
