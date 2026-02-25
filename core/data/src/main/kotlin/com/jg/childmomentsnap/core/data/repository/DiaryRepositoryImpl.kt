package com.jg.childmomentsnap.core.data.repository

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.util.DateUtils
import com.jg.childmomentsnap.core.data.datasource.DiaryLocalDataSource
import com.jg.childmomentsnap.core.data.datasource.GeminiApiRemoteDataSource
import com.jg.childmomentsnap.core.data.mapper.toDomain
import com.jg.childmomentsnap.core.data.mapper.toEntity
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import com.jg.childmomentsnap.core.model.Diary
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryLocalDataSource: DiaryLocalDataSource,
    private val geminiApiRemoteDataSource: GeminiApiRemoteDataSource
) : DiaryRepository {
    override suspend fun generateGeminiDairy(
        prompt: String
    ): DataResult<String> {
        return when (val response = geminiApiRemoteDataSource.generateDiary(prompt)) {
            is DataResult.Success -> DataResult.Success(response.data.analysisResult)
            is DataResult.Fail -> DataResult.Fail(
                code = response.code,
                message = response.message,
                throwable = response.throwable
            )
        }
    }

    override suspend fun setDiary(diary: Diary): DataResult<Boolean> {
        return try {
            diaryLocalDataSource.insertDiary(diary.toEntity())
            DataResult.Success(true)
        } catch (e: Exception) {
            //  TODO Error Code 정의 필요
            DataResult.Fail(-1, e.message, e)
        }
    }

    override suspend fun getDiariesByDate(startDate: String, endDate: String): DataResult<List<Diary>> {
        return try {
            val diaries = diaryLocalDataSource.getDiaryListByDate(startDate, endDate)
            DataResult.Success(data = diaries.map { it.toDomain() })
        } catch (e: Exception) {
            //  TODO Error Code 정의 필요
            DataResult.Fail(
                code = -1,
                message = e.message ?: "Unknown error",
                throwable = e
            )
        }
    }

    override suspend fun getDiariesByMonth(yearMonth: YearMonth): DataResult<List<Diary>> {
        val yearMonthString = DateUtils.formatYearMonth(yearMonth)
        return try {
            val diaries = diaryLocalDataSource.getDiaryList(yearMonthString)
            DataResult.Success(diaries.map { it.toDomain() })
        } catch (e: Exception) {
            //  TODO Error Code 정의 필요
            DataResult.Fail(-1, e.message, e)
        }
    }

    override suspend fun setFavorite(
        id: Long,
        isFavorite: Boolean
    ): DataResult<Boolean> {
        return try {
            diaryLocalDataSource.updateFavoriteStatus(id, isFavorite)
            DataResult.Success(true)
        } catch (e: Exception) {
            //  TODO Error Code 정의 필요
            DataResult.Fail(-1, e.message, e)
        }
    }
}
