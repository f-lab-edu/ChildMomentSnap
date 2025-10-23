package com.jg.childmomentsnap.core.network.model.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionResponseDto(
    val responses: List<VisionAnnotateImageResponseDto>
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
    val boundingPoly: VisionBoundingPolyDto,
    val fdBoundingPoly: VisionBoundingPolyDto,
    val landmarks: List<VisionLandmarkDto>,
    val rollAngle: Float,
    val panAngle: Float,
    val tiltAngle: Float,
    val detectionConfidence: Float,
    val landmarkingConfidence: Float,
    val joyLikelihood: VisionLikelihoodDto,
    val sorrowLikelihood: VisionLikelihoodDto,
    val angerLikelihood: VisionLikelihoodDto,
    val surpriseLikelihood: VisionLikelihoodDto,
    val underExposedLikelihood: VisionLikelihoodDto,
    val blurredLikelihood: VisionLikelihoodDto,
    val headwearLikelihood: VisionLikelihoodDto
)

@Serializable
data class VisionEntityAnnotationDto(
    val mid: String? = null,
    val locale: String? = null,
    val description: String,
    val score: Float,
    val confidence: Float? = null,
    val topicality: Float? = null,
    val boundingPoly: VisionBoundingPolyDto? = null
)

@Serializable
data class VisionLocalizedObjectAnnotationDto(
    val mid: String,
    val languageCode: String? = null,
    val name: String,
    val score: Float,
    val boundingPoly: VisionBoundingPolyDto
)

@Serializable
data class VisionBoundingPolyDto(
    val vertices: List<VisionVertexDto>,
    val normalizedVertices: List<VisionNormalizedVertexDto>? = null
)

@Serializable
data class VisionVertexDto(
    val x: Int,
    val y: Int
)

@Serializable
data class VisionNormalizedVertexDto(
    val x: Float,
    val y: Float
)

@Serializable
data class VisionLandmarkDto(
    val type: VisionLandmarkTypeDto,
    val position: VisionPositionDto
)

@Serializable
data class VisionPositionDto(
    val x: Float,
    val y: Float,
    val z: Float
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
    CHIN_RIGHT_GONION
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
    val code: Int,
    val message: String,
    val details: List<Map<String, String>>? = null
)
