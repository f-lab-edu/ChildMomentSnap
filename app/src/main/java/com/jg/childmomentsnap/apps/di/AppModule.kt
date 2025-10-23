package com.jg.childmomentsnap.apps.di

import com.jg.childmomentsnap.core.common.provider.ApiKeyProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindApiKeyProvider(apiKeyProvider: ApiKeyProviderImpl): ApiKeyProvider
}
