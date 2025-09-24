plugins {
    alias(libs.plugins.cms.android.library)
}

android {
    namespace = "com.jg.childmomentsnap.core.model"
}

dependencies {
    api(libs.kotlinx.datetime)
}