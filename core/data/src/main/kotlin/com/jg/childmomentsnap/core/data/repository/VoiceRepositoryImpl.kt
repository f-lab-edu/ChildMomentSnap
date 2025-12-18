package com.jg.childmomentsnap.core.data.repository

import com.jg.childmomentsnap.core.domain.repository.VoiceRepository
import com.jg.childmomentsnap.core.network.datasource.GoogleSpeechRemoteDataSource
import com.jg.childmomentsnap.core.network.model.speech.SpeechAudioDto
import com.jg.childmomentsnap.core.network.model.speech.SpeechConfigDto
import com.jg.childmomentsnap.core.network.model.speech.SpeechRequestDto
import com.jg.childmomentsnap.core.common.config.VoiceConfig
import java.io.File
import java.util.Base64
import javax.inject.Inject

class VoiceRepositoryImpl @Inject constructor(
    private val googleSpeechRemoteDataSource: GoogleSpeechRemoteDataSource
) : VoiceRepository {

    override suspend fun transcribe(voiceFile: File): String {
        val bytes = voiceFile.readBytes()
        val encodedContent = Base64.getEncoder().encodeToString(bytes)

        val request = SpeechRequestDto(
            config = SpeechConfigDto(
                encoding = VoiceConfig.AUDIO_ENCODING_API,
                sampleRateHertz = VoiceConfig.SAMPLE_RATE_HZ,
                languageCode = VoiceConfig.LANGUAGE_CODE,
                model = VoiceConfig.MODEL_TYPE
            ),
            audio = SpeechAudioDto(content = encodedContent)
        )

        val response = googleSpeechRemoteDataSource.recognizeSpeech(request)
        
        return response.results?.firstOrNull()?.alternatives?.firstOrNull()?.transcript ?: ""
    }
}
