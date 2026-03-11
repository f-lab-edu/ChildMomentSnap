package com.jg.childmomentsnap.core.data.datasource.local

import com.jg.childmomentsnap.database.entity.BabyEntity

interface BabyLocalDataSource {
    suspend fun insertBaby(baby: BabyEntity)
    suspend fun updateBaby(baby: BabyEntity)
    suspend fun deleteBaby(baby: BabyEntity)
    suspend fun getBabyList(userId: Long): List<BabyEntity>
    suspend fun getBabyByName(name: String): BabyEntity
}