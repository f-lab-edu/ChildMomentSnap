package com.jg.childmomentsnap.core.common.provider

interface AppInfoProvider {
    fun getPackageName(): String
    fun getCertificateFingerprint(): String?
}
