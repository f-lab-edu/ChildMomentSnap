package com.jg.childmomentsnap.core.network.di

import com.jg.childmomentsnap.core.network.datasource.GoogleSpeechRemoteDataSource
import com.jg.childmomentsnap.core.network.datasource.GoogleSpeechRemoteDataSourceImpl
import com.jg.childmomentsnap.core.network.datasource.GoogleVisionRemoteDataSource
import com.jg.childmomentsnap.core.network.datasource.GoogleVisionRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSource {

    @Binds
    abstract fun bindGoogleVisionRemoteDataSource(
        googleVisionRemoteDataSource: GoogleVisionRemoteDataSourceImpl
    ): GoogleVisionRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindGoogleSpeechRemoteDataSource(
        googleSpeechRemoteDataSource: GoogleSpeechRemoteDataSourceImpl
    ): GoogleSpeechRemoteDataSource

}