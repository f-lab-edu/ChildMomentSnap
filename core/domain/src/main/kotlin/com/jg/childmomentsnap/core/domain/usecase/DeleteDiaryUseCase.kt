package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import com.jg.childmomentsnap.core.model.Diary
import javax.inject.Inject

interface DeleteDiaryUseCase {
    suspend fun invoke(diary: Diary): DomainResult<Boolean, String>
}

class DeleteDiaryUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): DeleteDiaryUseCase {
    override suspend fun invoke(diary: Diary): DomainResult<Boolean, String> {
        return when(val result = diaryRepository.deleteDiary(diary)) {
            is DataResult.Success -> DomainResult.Success(data = true)
            is DataResult.Fail -> DomainResult.Fail(error = result.message ?: "")
        }
    }
}