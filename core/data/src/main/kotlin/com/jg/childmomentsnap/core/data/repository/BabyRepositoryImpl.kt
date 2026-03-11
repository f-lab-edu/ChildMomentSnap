package com.jg.childmomentsnap.core.data.repository

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.safeRunCatching
import com.jg.childmomentsnap.core.data.datasource.local.BabyLocalDataSource
import com.jg.childmomentsnap.core.data.mapper.toDomain
import com.jg.childmomentsnap.core.data.mapper.toEntity
import com.jg.childmomentsnap.core.domain.repository.BabyRepository
import com.jg.childmomentsnap.core.model.Baby
import javax.inject.Inject

class BabyRepositoryImpl @Inject constructor(
    private val babyLocalDataSource: BabyLocalDataSource
): BabyRepository {
    override suspend fun getBabies(userId: Long): DataResult<List<Baby>> {
        return safeRunCatching {
            babyLocalDataSource.getBabyList(userId)
        }.fold(
            onSuccess = { entities ->
                DataResult.Success(entities.map {
                    it.toDomain()
                })
            },
            onFailure = { error ->
                DataResult.Fail(
                    code = -1,
                    message = error.message ?: "Unknown error",
                    throwable = error
                )
            }
        )
    }

    override suspend fun setBabyInfo(baby: Baby): DataResult<Boolean> {
        return safeRunCatching {
            babyLocalDataSource.insertBaby(baby.toEntity())
        }.fold(
            onSuccess = { DataResult.Success(true) },
            onFailure = { error ->
                DataResult.Fail(
                    code = -1,
                    message = error.message ?: "Unknown error",
                    throwable = error
                )
            }
        )
    }

    override suspend fun updateBabyInfo(baby: Baby): DataResult<Boolean> {
        return safeRunCatching {
            babyLocalDataSource.updateBaby(baby.toEntity())
        }.fold(
            onSuccess = { DataResult.Success(true)},
            onFailure = { error ->
                DataResult.Fail(
                    code = -1,
                    message = error.message ?: "Unknown error",
                    throwable = error
                )
            }
        )
    }

    override suspend fun deleteBabyInfo(baby: Baby): DataResult<Boolean> {
        return safeRunCatching {
            babyLocalDataSource.deleteBaby(baby.toEntity())
        }.fold(
            onSuccess = { DataResult.Success(true)},
            onFailure = { error ->
                DataResult.Fail(
                    code = -1,
                    message = error.message ?: "Unknown error",
                    throwable = error
                )
            }
        )
    }
}