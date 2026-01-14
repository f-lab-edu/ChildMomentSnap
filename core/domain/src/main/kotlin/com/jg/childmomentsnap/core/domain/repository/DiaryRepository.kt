package com.jg.childmomentsnap.core.domain.repository

import com.jg.childmomentsnap.core.model.Diary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface DiaryRepository {
    fun getDiariesByDate(date: LocalDate): Flow<List<Diary>>
    fun getDiariesByMonth(yearMonth: YearMonth): Flow<List<Diary>>
}
