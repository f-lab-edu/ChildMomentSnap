package com.jg.childmomentsnap.core.data.datasource.remote

import com.jg.childmomentsnap.core.network.model.vision.VisionRequestDto
import com.jg.childmomentsnap.core.network.model.vision.VisionResponseDto

interface GoogleVisionRemoteDataSource {

    suspend fun analyzeImage(request: VisionRequestDto): VisionResponseDto
}
