package com.jg.childmomentsnap.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jg.childmomentsnap.database.dao.BabyDao
import com.jg.childmomentsnap.database.entity.BabyEntity

import com.jg.childmomentsnap.database.entity.UserEntity

@Database(
    entities = [BabyEntity::class, UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class BabyDatabase: RoomDatabase() {

    abstract fun babyDao(): BabyDao

    companion object {
        const val DATABASE_NAME = "baby_db"

        @Volatile
        private var Instance: BabyDatabase? = null

        fun getInstance(context: Context): BabyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = BabyDatabase::class.java,
                    name = DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}