package com.jg.childmomentsnap.core.data.datasource.local

import com.jg.childmomentsnap.database.dao.UserDao
import com.jg.childmomentsnap.database.entity.UserEntity
import javax.inject.Inject

class UserLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : UserLocalDataSource {
    override suspend fun insertUser(user: UserEntity) {
        return userDao.insertUser(user)
    }

    override suspend fun updateUser(user: UserEntity) {
        return userDao.updateUser(user)
    }

    override suspend fun deleteUser(user: UserEntity) {
        return userDao.deleteUser(user)
    }

    override suspend fun getUser(): UserEntity {
        return userDao.getUser()
    }

}