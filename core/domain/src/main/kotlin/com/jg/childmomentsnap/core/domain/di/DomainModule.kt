package com.jg.childmomentsnap.core.domain.di

import com.jg.childmomentsnap.core.domain.usecase.DeleteDiaryUseCase
import com.jg.childmomentsnap.core.domain.usecase.DeleteDiaryUseCaseImpl
import com.jg.childmomentsnap.core.domain.usecase.GeneratePhotoDiaryUseCase
import com.jg.childmomentsnap.core.domain.usecase.GeneratePhotoDiaryUseCaseImpl
import com.jg.childmomentsnap.core.domain.usecase.GetDiariesByDateUseCase
import com.jg.childmomentsnap.core.domain.usecase.GetDiariesByDateUseCaseImpl
import com.jg.childmomentsnap.core.domain.usecase.ToggleDiaryFavoriteUseCase
import com.jg.childmomentsnap.core.domain.usecase.ToggleDiaryFavoriteUseCaseImpl
import com.jg.childmomentsnap.core.domain.usecase.WriteDiaryUseCase
import com.jg.childmomentsnap.core.domain.usecase.WriteDiaryUseCaseImpl
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

    @Binds
    abstract fun bindWriteDiaryUseCase(
        writeDiaryUseCase: WriteDiaryUseCaseImpl
    ): WriteDiaryUseCase

    @Binds
    abstract fun bindGetDiariesByDateUseCase(
        getDiariesByDateUseCase: GetDiariesByDateUseCaseImpl
    ): GetDiariesByDateUseCase

    @Binds
    abstract fun bindToggleDairyFavoriteUseCase(
        toggleDiaryFavoriteUseCase: ToggleDiaryFavoriteUseCaseImpl
    ): ToggleDiaryFavoriteUseCase

    @Binds
    abstract fun bindDeleteDiaryUseCase(
        deleteDiaryUseCase: DeleteDiaryUseCaseImpl
    ): DeleteDiaryUseCase
}