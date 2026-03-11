package com.jg.childmomentsnap.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jg.childmomentsnap.database.entity.BabyEntity

@Dao
interface BabyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaby(baby: BabyEntity)

    @Update
    suspend fun updateBaby(baby: BabyEntity)

    @Delete
    suspend fun deleteBaby(baby: BabyEntity)

    @Query("SELECT * FROM baby_table WHERE user_id = :userId")
    suspend fun getBabyList(userId: Long): List<BabyEntity>

    @Query("SELECT * FROM baby_table WHERE name = :name")
    suspend fun getBabyByName(name: String): BabyEntity

}