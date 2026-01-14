import java.util.Properties

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

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

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties["CMS_STORE_FILE"] as String)
                storePassword = keystoreProperties["CMS_STORE_PASSWORD"] as String
                keyAlias = keystoreProperties["CMS_KEY_ALIAS"] as String
                keyPassword = keystoreProperties["CMS_KEY_PASSWORD"] as String
            }
        }
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
            // Debug 빌드에서는 기본 debug keystore 사용
            // signingConfig = signingConfigs.getByName("release")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Release 빌드에서만 keystore가 있을 때 release signing 사용
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
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
            excludes.add("**/OSGI-INF/MANIFEST.MF")
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/license.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/NOTICE.txt")
            excludes.add("META-INF/notice.txt")
            excludes.add("META-INF/ASL2.0")
            pickFirsts.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
            pickFirsts.add("META-INF/flogger_logger_backend_configuration.properties")
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
    implementation(projects.core.commonAndroid)
    implementation(projects.core.common)

    implementation(projects.feature.home)
    implementation(projects.feature.feed)
    implementation(projects.feature.diary)
    implementation(projects.feature.moment)


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
