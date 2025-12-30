package com.jg.childmomentsnap.core.data.datasource

import com.jg.childmomentsnap.database.dao.DiaryDao
import com.jg.childmomentsnap.database.entity.DiaryEntity
import javax.inject.Inject

class DiaryLocalDataSourceImpl @Inject constructor(
    private val diaryDao: DiaryDao
) : DiaryLocalDataSource {

    override suspend fun insertDiary(diary: DiaryEntity) {
        diaryDao.insertDiary(diary)
    }

    override suspend fun updateDiary(diary: DiaryEntity) {
        diaryDao.updateDiary(diary)
    }

    override suspend fun deleteDiary(diary: DiaryEntity) {
        diaryDao.deleteDiary(diary)
    }

    override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean) {
        diaryDao.updateFavoriteStatus(id, isFavorite)
    }

    override suspend fun getDiaryList(yearMonth: String): List<DiaryEntity> {
        return diaryDao.getDiaryList(yearMonth)
    }

    override suspend fun getDiaryListByDate(startDate: String, endDate: String): List<DiaryEntity> {
        return diaryDao.getDiaryListByDate(startDate, endDate)
    }

    override suspend fun getFavoriteDiaryList(): List<DiaryEntity> {
        return diaryDao.getFavoriteDiaryList()
    }

    override suspend fun searchDiary(query: String): List<DiaryEntity> {
        return diaryDao.searchDiary(query)
    }
}
