package com.jg.childmomentsnap.core.data.repository

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.safeRunCatching
import com.jg.childmomentsnap.core.common.util.DateUtils
import com.jg.childmomentsnap.core.data.datasource.local.DiaryLocalDataSource
import com.jg.childmomentsnap.core.data.datasource.remote.GeminiApiRemoteDataSource
import com.jg.childmomentsnap.core.data.mapper.toDomain
import com.jg.childmomentsnap.core.data.mapper.toEntity
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import com.jg.childmomentsnap.core.model.Diary
import java.time.YearMonth
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
        return safeRunCatching {
            diaryLocalDataSource.insertDiary(diary.toEntity())
        }.fold(
            onSuccess = { DataResult.Success(true) },
            onFailure = { e -> DataResult.Fail(-1, e.message ?: "Unknown error", e) } //  TODO Error Code 정의 필요
        )
    }

    override suspend fun getDiariesByDate(startDate: String, endDate: String): DataResult<List<Diary>> {
        return safeRunCatching {
            diaryLocalDataSource.getDiaryListByDate(startDate, endDate)
        }.fold(
            onSuccess = { diary -> DataResult.Success(diary.map { it.toDomain() }) },
            onFailure = { e ->
                //  TODO Error Code 정의 필요
                DataResult.Fail(
                    code = -1,
                    message = e.message ?: "Unknown error",
                    throwable = e
                )
            }
        )
    }

    override suspend fun getDiariesByMonth(yearMonth: YearMonth): DataResult<List<Diary>> {
        val yearMonthString = DateUtils.formatYearMonth(yearMonth)

        return safeRunCatching {
            diaryLocalDataSource.getDiaryList(yearMonthString)
        }.fold(
            onSuccess = { diaries ->
                DataResult.Success(diaries.map { it.toDomain() })
            },
            onFailure = { e ->
                //  TODO Error Code 정의 필요
                DataResult.Fail(-1, e.message, e)
            }
        )
    }

    override suspend fun setFavorite(
        id: Long,
        isFavorite: Boolean
    ): DataResult<Boolean> {
        return safeRunCatching {
            diaryLocalDataSource.updateFavoriteStatus(id, isFavorite)
        }.fold(
            onSuccess = { DataResult.Success(true)},
            onFailure = { e ->
                //  TODO Error Code 정의 필요
                DataResult.Fail(-1, e.message, e) }
        )
    }
}
