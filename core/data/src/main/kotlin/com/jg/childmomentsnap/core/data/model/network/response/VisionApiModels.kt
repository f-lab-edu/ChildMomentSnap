package com.jg.childmomentsnap.core.data.model.network.response

import kotlinx.serialization.Serializable

// ===== 응답 모델들 =====

@Serializable
data class VisionAnalysisResponse(
    val responses: List<AnnotateImageResponse>
)

@Serializable
data class AnnotateImageResponse(
    val faceAnnotations: List<FaceAnnotation>? = null,
    val labelAnnotations: List<EntityAnnotation>? = null,
    val textAnnotations: List<EntityAnnotation>? = null,
    val localizedObjectAnnotations: List<LocalizedObjectAnnotation>? = null,
    val error: Status? = null
)

@Serializable
data class FaceAnnotation(
    val boundingPoly: BoundingPoly,
    val fdBoundingPoly: BoundingPoly,
    val landmarks: List<Landmark>,
    val rollAngle: Float,
    val panAngle: Float,
    val tiltAngle: Float,
    val detectionConfidence: Float,
    val landmarkingConfidence: Float,
    // 표정 분석 (핵심 부분)
    val joyLikelihood: Likelihood,
    val sorrowLikelihood: Likelihood,
    val angerLikelihood: Likelihood,
    val surpriseLikelihood: Likelihood,
    val underExposedLikelihood: Likelihood,
    val blurredLikelihood: Likelihood,
    val headwearLikelihood: Likelihood
)

@Serializable
data class EntityAnnotation(
    val mid: String? = null,
    val locale: String? = null,
    val description: String,
    val score: Float,
    val confidence: Float? = null,
    val topicality: Float? = null,
    val boundingPoly: BoundingPoly? = null
)

@Serializable
data class LocalizedObjectAnnotation(
    val mid: String,
    val languageCode: String? = null,
    val name: String,
    val score: Float,
    val boundingPoly: BoundingPoly
)

@Serializable
data class BoundingPoly(
    val vertices: List<Vertex>,
    val normalizedVertices: List<NormalizedVertex>? = null
)

@Serializable
data class Vertex(
    val x: Int,
    val y: Int
)

@Serializable
data class NormalizedVertex(
    val x: Float,
    val y: Float
)

@Serializable
data class Landmark(
    val type: LandmarkType,
    val position: Position
)

@Serializable
data class Position(
    val x: Float,
    val y: Float,
    val z: Float
)

@Serializable
enum class LandmarkType {
    UNKNOWN_LANDMARK,
    LEFT_EYE,
    RIGHT_EYE,
    LEFT_OF_LEFT_EYEBROW,
    RIGHT_OF_LEFT_EYEBROW,
    LEFT_OF_RIGHT_EYEBROW,
    RIGHT_OF_RIGHT_EYEBROW,
    MIDPOINT_BETWEEN_EYES,
    NOSE_TIP,
    UPPER_LIP,
    LOWER_LIP,
    MOUTH_LEFT,
    MOUTH_RIGHT,
    MOUTH_CENTER,
    NOSE_BOTTOM_RIGHT,
    NOSE_BOTTOM_LEFT,
    NOSE_BOTTOM_CENTER,
    LEFT_EYE_TOP_BOUNDARY,
    LEFT_EYE_RIGHT_CORNER,
    LEFT_EYE_BOTTOM_BOUNDARY,
    LEFT_EYE_LEFT_CORNER,
    RIGHT_EYE_TOP_BOUNDARY,
    RIGHT_EYE_RIGHT_CORNER,
    RIGHT_EYE_BOTTOM_BOUNDARY,
    RIGHT_EYE_LEFT_CORNER,
    LEFT_EYEBROW_UPPER_MIDPOINT,
    RIGHT_EYEBROW_UPPER_MIDPOINT,
    LEFT_EAR_TRAGION,
    RIGHT_EAR_TRAGION,
    LEFT_EYE_PUPIL,
    RIGHT_EYE_PUPIL,
    FOREHEAD_GLABELLA,
    CHIN_GNATHION,
    CHIN_LEFT_GONION,
    CHIN_RIGHT_GONION
}

@Serializable
enum class Likelihood {
    UNKNOWN,
    VERY_UNLIKELY,
    UNLIKELY,
    POSSIBLE,
    LIKELY,
    VERY_LIKELY
}

@Serializable
data class Status(
    val code: Int,
    val message: String,
    val details: List<Map<String, String>>? = null
)