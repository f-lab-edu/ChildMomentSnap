package com.jg.childmomentsnap.database.di

import android.content.Context
import com.jg.childmomentsnap.database.dao.BabyDao
import com.jg.childmomentsnap.database.dao.DiaryDao
import com.jg.childmomentsnap.database.dao.UserDao
import com.jg.childmomentsnap.database.room.BabyDatabase
import com.jg.childmomentsnap.database.room.DiaryDatabase
import com.jg.childmomentsnap.database.room.UserDatabase
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

    @Provides
    @Singleton
    fun provideUserDatabase(
        @ApplicationContext context: Context
    ): UserDatabase {
        return UserDatabase.getInstance(context)
    }

    @Provides
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideBabyDatabase(
        @ApplicationContext context: Context
    ): BabyDatabase {
        return BabyDatabase.getInstance(context)
    }

    @Provides
    fun provideBabyDao(database: BabyDatabase): BabyDao {
        return database.babyDao()
    }
}
