package com.jg.childmomentsnap.core.data.repository

import com.jg.childmomentsnap.core.common.util.ImageEncoder
import com.jg.childmomentsnap.core.data.mapper.toDomain
import com.jg.childmomentsnap.core.domain.repository.PhotoRepository
import com.jg.childmomentsnap.core.model.VisionAnalysis
import com.jg.childmomentsnap.core.network.datasource.GoogleVisionRemoteDataSource
import com.jg.childmomentsnap.core.network.model.vision.VisionAnnotateImageRequestDto
import com.jg.childmomentsnap.core.network.model.vision.VisionFeatureDto
import com.jg.childmomentsnap.core.network.model.vision.VisionFeatureTypeDto
import com.jg.childmomentsnap.core.network.model.vision.VisionImageDto
import com.jg.childmomentsnap.core.network.model.vision.VisionRequestDto
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val remoteDataSource: GoogleVisionRemoteDataSource
) : PhotoRepository {
    override suspend fun analyzeImage(imageBytes: ByteArray): VisionAnalysis {
        val base64Image = ImageEncoder.bytesToBase64(imageBytes)

        val request = VisionRequestDto(
            requests = listOf(
                VisionAnnotateImageRequestDto(
                    image = VisionImageDto(content = base64Image),
                    features = listOf(
                        VisionFeatureDto(type = VisionFeatureTypeDto.FACE_DETECTION, maxResults = 10),
                        VisionFeatureDto(type = VisionFeatureTypeDto.LABEL_DETECTION, maxResults = 10),
                        VisionFeatureDto(type = VisionFeatureTypeDto.OBJECT_LOCALIZATION, maxResults = 10),
                        VisionFeatureDto(type = VisionFeatureTypeDto.TEXT_DETECTION, maxResults = 10)
                    )
                )
            )
        )

        val response = remoteDataSource.analyzeImage(request)
        val analysis = response.toDomain()

        if (analysis.errorMessage != null) {
            throw IllegalStateException("Vision API error: ${analysis.errorMessage}")
        }

        return analysis
    }
}

