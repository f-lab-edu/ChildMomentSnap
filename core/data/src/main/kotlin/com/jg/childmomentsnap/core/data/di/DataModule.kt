package com.jg.childmomentsnap.core.data.di

import com.jg.childmomentsnap.core.domain.repository.PhotoRepository
import com.jg.childmomentsnap.core.data.datasource.GoogleSpeechRemoteDataSource
import com.jg.childmomentsnap.core.data.datasource.GoogleSpeechRemoteDataSourceImpl
import com.jg.childmomentsnap.core.data.datasource.GoogleVisionRemoteDataSource
import com.jg.childmomentsnap.core.data.datasource.GoogleVisionRemoteDataSourceImpl
import com.jg.childmomentsnap.core.data.datasource.DiaryLocalDataSource
import com.jg.childmomentsnap.core.data.datasource.DiaryLocalDataSourceImpl
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

    @Binds
    abstract fun bindGoogleSpeechRemoteDataSource(
        googleSpeechRemoteDataSourceImpl: GoogleSpeechRemoteDataSourceImpl
    ): GoogleSpeechRemoteDataSource

    @Binds
    abstract fun bindGoogleVisionRemoteDataSource(
        googleVisionRemoteDataSourceImpl: GoogleVisionRemoteDataSourceImpl
    ): GoogleVisionRemoteDataSource

    @Binds
    abstract fun bindDiaryLocalDataSource(
        diaryLocalDataSourceImpl: DiaryLocalDataSourceImpl
    ): DiaryLocalDataSource

    @Binds
    abstract fun bindDiaryRepository(
        diaryRepositoryImpl: com.jg.childmomentsnap.core.data.repository.DiaryRepositoryImpl
    ): com.jg.childmomentsnap.core.domain.repository.DiaryRepository
}