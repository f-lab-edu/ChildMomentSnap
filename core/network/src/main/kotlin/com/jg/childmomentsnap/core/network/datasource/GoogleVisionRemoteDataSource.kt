package com.jg.childmomentsnap.core.network.datasource

import com.jg.childmomentsnap.core.network.model.vision.VisionRequestDto
import com.jg.childmomentsnap.core.network.model.vision.VisionResponseDto

interface GoogleVisionRemoteDataSource {

    suspend fun analyzeImage(request: VisionRequestDto): VisionResponseDto
}