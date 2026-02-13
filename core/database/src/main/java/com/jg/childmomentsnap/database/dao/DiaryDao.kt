package com.jg.childmomentsnap.database.dao

import kotlinx.coroutines.flow.Flow

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

    @Query("UPDATE diary_table SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM diary_table WHERE date LIKE :yearMonth || '%'")
    fun getDiaryList(yearMonth: String): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diary_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getDiaryListByDate(startDate: String, endDate: String): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diary_table WHERE is_favorite = 1")
    fun getFavoriteDiaryList(): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diary_table WHERE content LIKE '%' || :query || '%'")
    fun searchDiary(query: String): Flow<List<DiaryEntity>>
}