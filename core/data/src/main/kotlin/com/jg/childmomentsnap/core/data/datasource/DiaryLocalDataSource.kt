package com.jg.childmomentsnap.core.data.datasource

import com.jg.childmomentsnap.database.entity.DiaryEntity
import kotlinx.coroutines.flow.Flow

interface DiaryLocalDataSource {
    suspend fun insertDiary(diary: DiaryEntity)
    suspend fun updateDiary(diary: DiaryEntity)
    suspend fun deleteDiary(diary: DiaryEntity)
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    suspend fun getDiaryList(yearMonth: String): List<DiaryEntity>
    suspend fun getDiaryListByDate(startDate: String, endDate: String): List<DiaryEntity>
    suspend fun getFavoriteDiaryList(): List<DiaryEntity>
    suspend fun searchDiary(query: String): List<DiaryEntity>
}
