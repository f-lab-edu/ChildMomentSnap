package com.jg.childmomentsnap.core.data.datasource.local

import com.jg.childmomentsnap.database.entity.UserEntity

interface UserLocalDataSource {
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun deleteUser(user: UserEntity)
    suspend fun getUser(): UserEntity?
}