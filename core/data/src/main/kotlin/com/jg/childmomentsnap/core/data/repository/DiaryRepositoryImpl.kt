package com.jg.childmomentsnap.core.data.repository

import com.jg.childmomentsnap.core.data.datasource.DiaryLocalDataSource
import com.jg.childmomentsnap.core.data.mapper.toDomain
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import com.jg.childmomentsnap.core.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryLocalDataSource: DiaryLocalDataSource
) : DiaryRepository {

    override fun getDiariesByDate(date: LocalDate): Flow<List<Diary>> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE) // YYYY-MM-DD
        return diaryLocalDataSource.getDiaryListByDate(dateString, dateString).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getDiariesByMonth(yearMonth: YearMonth): Flow<List<Diary>> {
        val yearMonthString = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        return diaryLocalDataSource.getDiaryList(yearMonthString).map { list ->
            list.map { it.toDomain() }
        }
    }
}
