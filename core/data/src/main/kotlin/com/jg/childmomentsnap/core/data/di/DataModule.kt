package com.jg.childmomentsnap.core.data.di

import com.jg.childmomentsnap.core.data.repository.PhotoRepository
import com.jg.childmomentsnap.core.data.repository.PhotoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindsPhotoRepository(
        photoRepository: PhotoRepositoryImpl
    ): PhotoRepository

}