package com.jg.childmomentsnap.core.data.datasource

import com.jg.childmomentsnap.database.entity.DiaryEntity
import kotlinx.coroutines.flow.Flow

interface DiaryLocalDataSource {
    suspend fun insertDiary(diary: DiaryEntity)
    suspend fun updateDiary(diary: DiaryEntity)
    suspend fun deleteDiary(diary: DiaryEntity)
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    fun getDiaryList(yearMonth: String): Flow<List<DiaryEntity>>
    fun getDiaryListByDate(startDate: String, endDate: String): Flow<List<DiaryEntity>>
    fun getFavoriteDiaryList(): Flow<List<DiaryEntity>>
    fun searchDiary(query: String): Flow<List<DiaryEntity>>
}
