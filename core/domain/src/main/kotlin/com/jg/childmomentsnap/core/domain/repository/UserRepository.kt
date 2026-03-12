package com.jg.childmomentsnap.core.domain.repository

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.model.User

interface UserRepository {
    suspend fun getUser(): DataResult<User>
    suspend fun setUser(user: User): DataResult<Boolean>
    suspend fun updateUser(user: User): DataResult<Boolean>
    suspend fun deleteUser(user: User): DataResult<Boolean>
}