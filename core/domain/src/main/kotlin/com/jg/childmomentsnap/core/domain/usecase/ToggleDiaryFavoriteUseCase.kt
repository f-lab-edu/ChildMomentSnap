package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import javax.inject.Inject

interface ToggleDiaryFavoriteUseCase {
    suspend fun invoke(id: Long, isFavorite: Boolean): DomainResult<Boolean, String>
}

class ToggleDiaryFavoriteUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): ToggleDiaryFavoriteUseCase {
    override suspend fun invoke(
        id: Long,
        isFavorite: Boolean
    ): DomainResult<Boolean, String> {
        return when (val result = diaryRepository.setFavorite(id, isFavorite)){
            is DataResult.Success -> DomainResult.Success(data = result.data)
            is DataResult.Fail -> {
                DomainResult.Fail(result.message ?: "")
            }
        }
    }
}