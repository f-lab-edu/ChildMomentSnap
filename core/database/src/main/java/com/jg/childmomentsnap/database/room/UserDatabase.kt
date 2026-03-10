package com.jg.childmomentsnap.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jg.childmomentsnap.database.dao.UserDao
import com.jg.childmomentsnap.database.entity.UserEntity
import kotlin.concurrent.Volatile

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class UserDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "user_db"

        @Volatile
        private var Instance: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = UserDatabase::class.java,
                    name = DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}