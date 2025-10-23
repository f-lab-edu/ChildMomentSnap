package com.jg.childmomentsnap.core.model

import kotlinx.serialization.Serializable

@Serializable
data class VisionAnalysis(
    val labels: List<VisionLabel>,
    val objects: List<VisionObject>,
    val faces: List<VisionFaceEmotion>,
    val detectedText: String?,
    val errorMessage: String? = null
)

@Serializable
data class VisionLabel(
    val description: String,
    val score: Float,
    val topicality: Float?
)

@Serializable
data class VisionObject(
    val name: String,
    val score: Float
)

@Serializable
data class VisionFaceEmotion(
    val detectionConfidence: Float,
    val joy: VisionLikelihood,
    val sorrow: VisionLikelihood,
    val anger: VisionLikelihood,
    val surprise: VisionLikelihood
)

@Serializable
enum class VisionLikelihood {
    UNKNOWN,
    VERY_UNLIKELY,
    UNLIKELY,
    POSSIBLE,
    LIKELY,
    VERY_LIKELY
}
