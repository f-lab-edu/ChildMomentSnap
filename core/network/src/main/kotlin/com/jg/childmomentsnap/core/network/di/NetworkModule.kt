package com.jg.childmomentsnap.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jg.childmomentsnap.core.common.config.NetworkHeaderKey
import com.jg.childmomentsnap.core.common.provider.AppInfoProvider
import com.jg.childmomentsnap.core.network.BuildConfig
import com.jg.childmomentsnap.core.network.api.GoogleSpeechApiService
import com.jg.childmomentsnap.core.network.api.GoogleVisionApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(
        appInfoProvider: AppInfoProvider
    ): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.addHeader(NetworkHeaderKey.X_ANDROID_PACKAGE, appInfoProvider.getPackageName())

            val certFingerprint = appInfoProvider.getCertificateFingerprint()
            if (certFingerprint != null) {
                requestBuilder.addHeader(NetworkHeaderKey.X_ANDROID_CERT, certFingerprint)
            }
            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GOOGLE_VISION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleVisionApiService(retrofit: Retrofit): GoogleVisionApiService {
        return retrofit.create(GoogleVisionApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("Speech")
    fun provideSpeechRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GOOGLE_SPEECH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleSpeechApiService(@Named("Speech") retrofit: Retrofit): GoogleSpeechApiService {
        return retrofit.create(GoogleSpeechApiService::class.java)
    }
}
