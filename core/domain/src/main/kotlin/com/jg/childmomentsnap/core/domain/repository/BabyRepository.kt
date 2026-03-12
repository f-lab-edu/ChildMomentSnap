package com.jg.childmomentsnap.core.domain.repository

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.model.Baby

interface BabyRepository {
    suspend fun getBabies(userId: Long): DataResult<List<Baby>>
    suspend fun setBabyInfo(baby: Baby): DataResult<Boolean>
    suspend fun updateBabyInfo(baby: Baby): DataResult<Boolean>
    suspend fun deleteBabyInfo(baby: Baby): DataResult<Boolean>
}