package com.jg.childmomentsnap.core.data.datasource.remote

import com.jg.childmomentsnap.core.common.provider.ApiKeyProvider
import com.jg.childmomentsnap.core.network.api.GoogleSpeechApiService
import com.jg.childmomentsnap.core.network.model.speech.SpeechRequestDto
import com.jg.childmomentsnap.core.network.model.speech.SpeechResponseDto
import javax.inject.Inject

class GoogleSpeechRemoteDataSourceImpl @Inject constructor(
    private val googleSpeechApiService: GoogleSpeechApiService,
    private val apiKeyProvider: ApiKeyProvider
) : GoogleSpeechRemoteDataSource {
    override suspend fun recognizeSpeech(request: SpeechRequestDto): SpeechResponseDto =
        googleSpeechApiService.recognizeSpeech(apiKeyProvider.getVisionApiKey(), request)
}
