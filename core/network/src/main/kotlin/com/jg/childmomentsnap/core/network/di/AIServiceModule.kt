package com.jg.childmomentsnap.core.network.di

import com.jg.childmomentsnap.core.network.service.GeminiApiService
import com.jg.childmomentsnap.core.network.service.GeminiApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AIServiceModule {
    @Binds
    abstract fun bindGeminiApiService(geminiApiService: GeminiApiServiceImpl): GeminiApiService
}