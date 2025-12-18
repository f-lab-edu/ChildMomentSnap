package com.jg.childmomentsnap.core.common.android.di

import com.jg.childmomentsnap.core.common.provider.AppInfoProvider
import com.jg.childmomentsnap.core.common.android.provider.AndroidAppInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonAndroidModule {

    @Binds
    abstract fun bindAppInfoProvider(
        androidAppInfoProvider: AndroidAppInfoProvider
    ): AppInfoProvider
}
