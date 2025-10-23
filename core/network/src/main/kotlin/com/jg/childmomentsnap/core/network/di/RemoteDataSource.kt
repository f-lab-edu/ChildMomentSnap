package com.jg.childmomentsnap.core.network.di

import com.jg.childmomentsnap.core.network.datasource.GoogleVisionRemoteDataSource
import com.jg.childmomentsnap.core.network.datasource.GoogleVisionRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSource {

    @Binds
    abstract fun bindGoogleVisionRemoteDataSource(
        googleVisionRemoteDataSource: GoogleVisionRemoteDataSourceImpl
    ): GoogleVisionRemoteDataSource

}