package com.jg.childmomentsnap.apps.di

import com.jg.childmomentsnap.apps.BuildConfig
import com.jg.childmomentsnap.core.common.provider.ApiKeyProvider
import javax.inject.Inject

class ApiKeyProviderImpl @Inject constructor() : ApiKeyProvider {
    override fun getVisionApiKey(): String {
        return BuildConfig.GOOGLE_CLOUD_API_KEY
    }
}
