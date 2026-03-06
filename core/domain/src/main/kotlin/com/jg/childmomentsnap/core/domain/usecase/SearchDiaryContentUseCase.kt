package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import com.jg.childmomentsnap.core.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SearchDiaryContentUseCase {
    fun invoke(query: String): Flow<DomainResult<List<Diary>, String>>
}

class SearchDiaryContentUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): SearchDiaryContentUseCase {
    override fun invoke(query: String): Flow<DomainResult<List<Diary>, String>> {
        return diaryRepository.searchDiary(query).map { result ->
            when (result) {
                is DataResult.Success -> DomainResult.Success(result.data)
                is DataResult.Fail -> DomainResult.Fail(result.message ?: "")
            }
        }
    }
}