package com.jg.childmomentsnap.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jg.childmomentsnap.database.entity.DiaryEntity

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: DiaryEntity)

    @Update
    suspend fun updateDiary(diary: DiaryEntity)

    @Delete
    suspend fun deleteDiary(diary: DiaryEntity)

    @Update
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM diary_table WHERE date LIKE :yearMonth || '%'")
    suspend fun getDiaryList(yearMonth: String): List<DiaryEntity>

    @Query("SELECT * FROM diary_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, time DESC")
    suspend fun getDiaryListByDate(startDate: String, endDate: String): List<DiaryEntity>

    @Query("SELECT * FROM diary_table WHERE is_favorite = 1")
    suspend fun getFavoriteDiaryList(): List<DiaryEntity>

    @Query("SELECT * FROM diary_table WHERE content LIKE '%' || :query || '%'")
    suspend fun searchDiary(query: String): List<DiaryEntity>
}