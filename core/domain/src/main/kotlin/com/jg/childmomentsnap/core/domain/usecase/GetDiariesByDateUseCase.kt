package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.common.util.DateUtils
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import com.jg.childmomentsnap.core.model.Diary
import java.time.LocalDate
import javax.inject.Inject

interface GetDiariesByDateUseCase {
    suspend fun invoke(date: LocalDate): DomainResult<List<Diary>, String>
}

class GetDiariesByDateUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
) : GetDiariesByDateUseCase {
    override suspend fun invoke(date: LocalDate): DomainResult<List<Diary>, String> {
        val startDate = DateUtils.getStartOfDay(date)
        val endDate = DateUtils.getEndOfDay(date)

        return when (val result = diaryRepository.getDiariesByDate(
            startDate = startDate,
            endDate = endDate
        )) {
            is DataResult.Success -> {
                DomainResult.Success(result.data)
            }

            is DataResult.Fail -> {
                DomainResult.Fail(result.message ?: "")
            }
        }
    }
}