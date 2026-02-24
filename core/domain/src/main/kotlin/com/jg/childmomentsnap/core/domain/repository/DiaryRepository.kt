package com.jg.childmomentsnap.core.domain.repository

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.model.Diary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface DiaryRepository {
    suspend fun generateGeminiDairy(prompt: String): DataResult<String>
    suspend fun setDiary(diary: Diary): DataResult<Boolean>
    suspend fun getDiariesByDate(startDate: String, endDate: String): DataResult<List<Diary>>
    suspend fun getDiariesByMonth(yearMonth: YearMonth): DataResult<List<Diary>>
}

