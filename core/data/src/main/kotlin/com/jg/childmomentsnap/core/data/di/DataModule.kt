package com.jg.childmomentsnap.core.data.di

import com.jg.childmomentsnap.core.domain.repository.PhotoRepository
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
    abstract fun bindPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): PhotoRepository

    @Binds
    abstract fun bindVoiceRepository(
        voiceRepositoryImpl: com.jg.childmomentsnap.core.data.repository.VoiceRepositoryImpl
    ): com.jg.childmomentsnap.core.domain.repository.VoiceRepository
}