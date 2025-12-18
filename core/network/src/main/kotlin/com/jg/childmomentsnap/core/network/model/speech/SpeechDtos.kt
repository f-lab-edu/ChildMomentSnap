package com.jg.childmomentsnap.core.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class SpeechRequestDto(
    val config: SpeechConfigDto,
    val audio: SpeechAudioDto
)

@Serializable
data class SpeechConfigDto(
    val encoding: String = "AMR_WB",
    val sampleRateHertz: Int = 16000,
    val languageCode: String = "ko-KR",
    val model: String = "chirp_3"
)

@Serializable
data class SpeechAudioDto(
    val content: String
)

@Serializable
data class SpeechResponseDto(
    val results: List<SpeechRecognitionResultDto>? = null
)

@Serializable
data class SpeechRecognitionResultDto(
    val alternatives: List<SpeechRecognitionAlternativeDto>
)

@Serializable
data class SpeechRecognitionAlternativeDto(
    val transcript: String,
    val confidence: Float
)
