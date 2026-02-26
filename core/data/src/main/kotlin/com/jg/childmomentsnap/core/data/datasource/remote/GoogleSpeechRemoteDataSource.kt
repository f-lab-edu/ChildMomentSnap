package com.jg.childmomentsnap.core.data.datasource.remote

import com.jg.childmomentsnap.core.network.model.speech.SpeechRequestDto
import com.jg.childmomentsnap.core.network.model.speech.SpeechResponseDto

interface GoogleSpeechRemoteDataSource {
    suspend fun recognizeSpeech(request: SpeechRequestDto): SpeechResponseDto
}
