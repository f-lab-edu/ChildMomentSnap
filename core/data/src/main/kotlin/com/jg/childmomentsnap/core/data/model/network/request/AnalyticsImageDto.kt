package com.jg.childmomentsnap.core.data.model.network.request

import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsImageDto(
    val requests: List<AnnotateImageRequest>
)

@Serializable
data class AnnotateImageRequest(
    val image: Image,
    val features: List<Feature>
)

@Serializable
data class Image(
    val content: String? = null,  // Base64 인코딩된 이미지
    val source: ImageSource? = null  // 또는 Google Cloud Storage URI
)

@Serializable
data class ImageSource(
    val gcsImageUri: String
)

@Serializable
data class Feature(
    val type: FeatureType,
    val maxResults: Int? = null
)

@Serializable
enum class FeatureType {
    FACE_DETECTION,
    LABEL_DETECTION,
    TEXT_DETECTION,
    OBJECT_LOCALIZATION
}
