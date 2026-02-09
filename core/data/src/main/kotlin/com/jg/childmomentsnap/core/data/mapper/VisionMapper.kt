package com.jg.childmomentsnap.core.data.mapper

import com.jg.childmomentsnap.core.model.VisionAnalysis
import com.jg.childmomentsnap.core.model.VisionFaceEmotion
import com.jg.childmomentsnap.core.model.VisionLabel
import com.jg.childmomentsnap.core.model.VisionLikelihood
import com.jg.childmomentsnap.core.model.VisionObject
import com.jg.childmomentsnap.core.network.model.vision.VisionEntityAnnotationDto
import com.jg.childmomentsnap.core.network.model.vision.VisionFaceAnnotationDto
import com.jg.childmomentsnap.core.network.model.vision.VisionLikelihoodDto
import com.jg.childmomentsnap.core.network.model.vision.VisionLocalizedObjectAnnotationDto
import com.jg.childmomentsnap.core.network.model.vision.VisionResponseDto

fun VisionResponseDto.toDomain(): VisionAnalysis {
    val flattenedResponses = responses

    val labels = flattenedResponses
        .flatMap { it.labelAnnotations.orEmpty() }
        .mapNotNull { it.toDomainLabelOrNull() }

    val objects = flattenedResponses
        .flatMap { it.localizedObjectAnnotations.orEmpty() }
        .mapNotNull { it.toDomainObjectOrNull() }

    val faces = flattenedResponses
        .flatMap { it.faceAnnotations.orEmpty() }
        .map { it.toDomainFace() }

    val detectedText = flattenedResponses
        .flatMap { it.textAnnotations.orEmpty() }
        .firstOrNull()?.description

    val errorMessage = flattenedResponses.firstNotNullOfOrNull { it.error?.message }

    return VisionAnalysis(
        labels = labels,
        objects = objects,
        faces = faces,
        detectedText = detectedText,
        errorMessage = errorMessage
    )
}

private fun VisionEntityAnnotationDto.toDomainLabelOrNull(): VisionLabel? {
    val desc = description ?: return null
    return VisionLabel(
        description = desc,
        score = score ?: 0f,
        topicality = topicality
    )
}

private fun VisionLocalizedObjectAnnotationDto.toDomainObjectOrNull(): VisionObject? {
    val objectName = name ?: return null
    return VisionObject(
        name = objectName,
        score = score ?: 0f
    )
}

private fun VisionFaceAnnotationDto.toDomainFace(): VisionFaceEmotion =
    VisionFaceEmotion(
        detectionConfidence = detectionConfidence ?: 0f,
        joy = joyLikelihood.toDomain(),
        sorrow = sorrowLikelihood.toDomain(),
        anger = angerLikelihood.toDomain(),
        surprise = surpriseLikelihood.toDomain()
    )

private fun VisionLikelihoodDto?.toDomain(): VisionLikelihood = when (this) {
    VisionLikelihoodDto.UNKNOWN, null -> VisionLikelihood.UNKNOWN
    VisionLikelihoodDto.VERY_UNLIKELY -> VisionLikelihood.VERY_UNLIKELY
    VisionLikelihoodDto.UNLIKELY -> VisionLikelihood.UNLIKELY
    VisionLikelihoodDto.POSSIBLE -> VisionLikelihood.POSSIBLE
    VisionLikelihoodDto.LIKELY -> VisionLikelihood.LIKELY
    VisionLikelihoodDto.VERY_LIKELY -> VisionLikelihood.VERY_LIKELY
}

