package com.jg.childmomentsnap.core.network.api

import com.jg.childmomentsnap.core.network.model.speech.SpeechRequestDto
import com.jg.childmomentsnap.core.network.model.speech.SpeechResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleSpeechApiService {
    @POST("v1/speech:recognize")
    suspend fun recognizeSpeech(
        @Query("key") apiKey: String,
        @Body request: SpeechRequestDto
    ): SpeechResponseDto
}
