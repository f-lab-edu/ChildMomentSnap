package com.jg.childmomentsnap.core.data.datasource.local

import com.jg.childmomentsnap.database.dao.BabyDao
import com.jg.childmomentsnap.database.entity.BabyEntity
import javax.inject.Inject

class BabyLocalDataSourceImpl @Inject constructor(
    private val babyDao: BabyDao
): BabyLocalDataSource {

    override suspend fun insertBaby(baby: BabyEntity) {
        return babyDao.insertBaby(baby)
    }

    override suspend fun updateBaby(baby: BabyEntity) {
        return babyDao.updateBaby(baby)
    }

    override suspend fun deleteBaby(baby: BabyEntity) {
        return babyDao.deleteBaby(baby)
    }

    override suspend fun getBabyList(userId: Long): List<BabyEntity> {
        return babyDao.getBabyList(userId)
    }

    override suspend fun getBabyByName(name: String): BabyEntity {
        return babyDao.getBabyByName(name)
    }
}