package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import com.jg.childmomentsnap.core.model.Diary
import javax.inject.Inject

interface WriteDiaryUseCase {
    suspend operator fun invoke(diary: Diary): DomainResult<Boolean, String>
}

class WriteDiaryUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): WriteDiaryUseCase {
    override suspend fun invoke(diary: Diary): DomainResult<Boolean, String> {
        return when(val result = diaryRepository.setDiary(diary)) {
            is DataResult.Success -> DomainResult.Success(result.data)
            is DataResult.Fail -> DomainResult.Fail(result.message ?: "")
        }
    }
}