package com.jg.childmomentsnap.core.data.repository

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.safeRunCatching
import com.jg.childmomentsnap.core.data.datasource.local.UserLocalDataSource
import com.jg.childmomentsnap.core.data.mapper.toDomain
import com.jg.childmomentsnap.core.data.mapper.toEntity
import com.jg.childmomentsnap.core.domain.repository.UserRepository
import com.jg.childmomentsnap.core.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource
): UserRepository {
    override suspend fun getUser(): DataResult<User> {
        return safeRunCatching {
            userLocalDataSource.getUser()
        }.fold(
            onSuccess = { userEntity ->
                DataResult.Success(userEntity.toDomain()) },
            onFailure = { error ->
                DataResult.Fail(
                    code = -1,
                    message = error.message ?: "Unknown error",
                    throwable = error
                )
            }
        )
    }

    override suspend fun setUser(user: User): DataResult<Boolean> {
        return safeRunCatching {
            userLocalDataSource.insertUser(user.toEntity())
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

    override suspend fun updateUser(user: User): DataResult<Boolean> {
        return safeRunCatching {
            userLocalDataSource.updateUser(user.toEntity())
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

    override suspend fun deleteUser(user: User): DataResult<Boolean> {
        return safeRunCatching {
            userLocalDataSource.deleteUser(user.toEntity())
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
}