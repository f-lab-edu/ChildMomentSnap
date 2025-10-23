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
        .map { it.toDomainLabel() }

    val objects = flattenedResponses
        .flatMap { it.localizedObjectAnnotations.orEmpty() }
        .map { it.toDomainObject() }

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

private fun VisionEntityAnnotationDto.toDomainLabel(): VisionLabel =
    VisionLabel(
        description = description,
        score = score,
        topicality = topicality
    )

private fun VisionLocalizedObjectAnnotationDto.toDomainObject(): VisionObject =
    VisionObject(
        name = name,
        score = score
    )

private fun VisionFaceAnnotationDto.toDomainFace(): VisionFaceEmotion =
    VisionFaceEmotion(
        detectionConfidence = detectionConfidence,
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
