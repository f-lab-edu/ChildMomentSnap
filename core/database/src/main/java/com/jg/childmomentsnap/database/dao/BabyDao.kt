package com.jg.childmomentsnap.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

}