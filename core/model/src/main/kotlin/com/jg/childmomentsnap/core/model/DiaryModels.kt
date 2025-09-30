package com.jg.childmomentsnap.core.model

import kotlinx.serialization.Serializable


/**
 * 일기 생성을 위한 표정 분석 결과
 */
@Serializable
data class EmotionAnalysisResult(
    val dominantEmotion: EmotionType,
    val emotionScores: Map<EmotionType, Float>,
    val confidence: Float,
    val faceCount: Int,
    val detectedObjects: List<String> = emptyList(),
    val detectedText: String? = null
)

@Serializable
enum class EmotionType {
    JOY,
    SADNESS,
    ANGER,
    SURPRISE,
    NEUTRAL;

    fun toKorean(): String = when(this) {
        JOY -> "기쁨"
        SADNESS -> "슬픔"
        ANGER -> "화남"
        SURPRISE -> "놀람"
        NEUTRAL -> "평온"
    }
}

/**
 * 일기 생성을 위한 컨텍스트
 */
@Serializable
data class DiaryGenerationContext(
    val emotionAnalysis: EmotionAnalysisResult,
    val imageDescription: String,
    val timestamp: String,
    val voiceTranscript: String? = null
)