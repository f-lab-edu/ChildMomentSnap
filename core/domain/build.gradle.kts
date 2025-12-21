plugins {
    alias(libs.plugins.cms.android.library)
    alias(libs.plugins.cms.hilt)
}

android {
    namespace = "com.jg.childmomentsnap.core.domain"
}

dependencies {
    implementation(projects.core.model)

}
