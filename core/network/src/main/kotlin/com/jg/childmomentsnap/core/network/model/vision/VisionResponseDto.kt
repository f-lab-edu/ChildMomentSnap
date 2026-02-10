package com.jg.childmomentsnap.core.network.model.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionResponseDto(
    val responses: List<VisionAnnotateImageResponseDto> = emptyList()
)

@Serializable
data class VisionAnnotateImageResponseDto(
    val faceAnnotations: List<VisionFaceAnnotationDto>? = null,
    val labelAnnotations: List<VisionEntityAnnotationDto>? = null,
    val textAnnotations: List<VisionEntityAnnotationDto>? = null,
    val localizedObjectAnnotations: List<VisionLocalizedObjectAnnotationDto>? = null,
    val error: VisionStatusDto? = null
)

@Serializable
data class VisionFaceAnnotationDto(
    val boundingPoly: VisionBoundingPolyDto? = null,
    val fdBoundingPoly: VisionBoundingPolyDto? = null,
    val landmarks: List<VisionLandmarkDto>? = null,
    val rollAngle: Float? = null,
    val panAngle: Float? = null,
    val tiltAngle: Float? = null,
    val detectionConfidence: Float? = null,
    val landmarkingConfidence: Float? = null,
    val joyLikelihood: VisionLikelihoodDto? = null,
    val sorrowLikelihood: VisionLikelihoodDto? = null,
    val angerLikelihood: VisionLikelihoodDto? = null,
    val surpriseLikelihood: VisionLikelihoodDto? = null,
    val underExposedLikelihood: VisionLikelihoodDto? = null,
    val blurredLikelihood: VisionLikelihoodDto? = null,
    val headwearLikelihood: VisionLikelihoodDto? = null
)

@Serializable
data class VisionEntityAnnotationDto(
    val mid: String? = null,
    val locale: String? = null,
    val description: String? = null,
    val score: Float? = null,
    val confidence: Float? = null,
    val topicality: Float? = null,
    val boundingPoly: VisionBoundingPolyDto? = null
)

@Serializable
data class VisionLocalizedObjectAnnotationDto(
    val mid: String? = null,
    val languageCode: String? = null,
    val name: String? = null,
    val score: Float? = null,
    val boundingPoly: VisionBoundingPolyDto? = null
)

@Serializable
data class VisionBoundingPolyDto(
    val vertices: List<VisionVertexDto>? = null,
    val normalizedVertices: List<VisionNormalizedVertexDto>? = null
)

@Serializable
data class VisionVertexDto(
    val x: Int? = null,
    val y: Int? = null
)

@Serializable
data class VisionNormalizedVertexDto(
    val x: Float? = null,
    val y: Float? = null
)

@Serializable
data class VisionLandmarkDto(
    val type: VisionLandmarkTypeDto? = null,
    val position: VisionPositionDto? = null
)

@Serializable
data class VisionPositionDto(
    val x: Float? = null,
    val y: Float? = null,
    val z: Float? = null
)

@Serializable
enum class VisionLandmarkTypeDto {
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
    CHIN_RIGHT_GONION,
    LEFT_CHEEK_CENTER,
    RIGHT_CHEEK_CENTER
}

@Serializable
enum class VisionLikelihoodDto {
    UNKNOWN,
    VERY_UNLIKELY,
    UNLIKELY,
    POSSIBLE,
    LIKELY,
    VERY_LIKELY
}

@Serializable
data class VisionStatusDto(
    val code: Int? = null,
    val message: String? = null,
    val details: List<Map<String, String>>? = null
)

