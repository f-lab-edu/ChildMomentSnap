package com.jg.childmomentsnap.core.network.model.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionRequestDto(
    val requests: List<VisionAnnotateImageRequestDto>
)

@Serializable
data class VisionAnnotateImageRequestDto(
    val image: VisionImageDto,
    val features: List<VisionFeatureDto>
)

@Serializable
data class VisionImageDto(
    val content: String? = null,
    val source: VisionImageSourceDto? = null
)

@Serializable
data class VisionImageSourceDto(
    val gcsImageUri: String
)

@Serializable
data class VisionFeatureDto(
    val type: VisionFeatureTypeDto,
    val maxResults: Int? = null
)

@Serializable
enum class VisionFeatureTypeDto {
    FACE_DETECTION,
    LABEL_DETECTION,
    TEXT_DETECTION,
    OBJECT_LOCALIZATION
}
