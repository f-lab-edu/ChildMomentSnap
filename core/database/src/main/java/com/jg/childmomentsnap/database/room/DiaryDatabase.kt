package com.jg.childmomentsnap.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jg.childmomentsnap.database.dao.DiaryDao
import com.jg.childmomentsnap.database.entity.DiaryEntity

@Database(
    entities = [DiaryEntity::class],
    version = 2,
    exportSchema = true
)
abstract class DiaryDatabase : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao

    companion object {
        const val DATABASE_NAME = "diary_db"

        @Volatile
        private var Instance: DiaryDatabase? = null

        fun getInstance(context: Context): DiaryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = DiaryDatabase::class.java,
                    name = DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}