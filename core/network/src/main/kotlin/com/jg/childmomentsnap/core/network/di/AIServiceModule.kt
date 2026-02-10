package com.jg.childmomentsnap.core.network.di

import com.google.ai.client.generativeai.GenerativeModel
import com.jg.childmomentsnap.core.common.provider.ApiKeyProvider
import com.jg.childmomentsnap.core.network.service.GeminiApiService
import com.jg.childmomentsnap.core.network.service.GeminiApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AIServiceBindingModule {
    @Binds
    abstract fun bindGeminiApiService(geminiApiService: GeminiApiServiceImpl): GeminiApiService
}

@Module
@InstallIn(SingletonComponent::class)
object AIServiceProviderModule {
    private const val modelName = "gemini-2.0-flash"
    @Provides
    @Singleton
    fun provideGenerativeModel(apiKeyProvider: ApiKeyProvider): GenerativeModel {
        return GenerativeModel(
            modelName = modelName,
            apiKey = apiKeyProvider.getGeminiApiKey()
        )
    }
}