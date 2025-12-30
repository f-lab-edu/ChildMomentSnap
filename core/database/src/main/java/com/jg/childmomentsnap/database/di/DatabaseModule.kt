package com.jg.childmomentsnap.database.di

import android.content.Context
import com.jg.childmomentsnap.database.dao.DiaryDao
import com.jg.childmomentsnap.database.room.DiaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDiaryDatabase(
        @ApplicationContext context: Context,
    ): DiaryDatabase {
        return DiaryDatabase.getInstance(context)
    }

    @Provides
    fun provideDiaryDao(database: DiaryDatabase): DiaryDao {
        return database.diaryDao()
    }
}
