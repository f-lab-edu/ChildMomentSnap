package com.jg.childmomentsnap.core.domain.di

import com.jg.childmomentsnap.core.domain.usecase.GeneratePhotoDiaryUseCase
import com.jg.childmomentsnap.core.domain.usecase.GeneratePhotoDiaryUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindGeneratePhotoDiaryUseCase(
        generatePhotoDiaryUseCase: GeneratePhotoDiaryUseCaseImpl
    ): GeneratePhotoDiaryUseCase

}