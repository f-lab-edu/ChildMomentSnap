package com.jg.childmomentsnap.core.data.datasource

import com.jg.childmomentsnap.core.common.provider.ApiKeyProvider
import com.jg.childmomentsnap.core.network.api.GoogleVisionApiService
import com.jg.childmomentsnap.core.network.model.vision.VisionRequestDto
import com.jg.childmomentsnap.core.network.model.vision.VisionResponseDto
import javax.inject.Inject

class GoogleVisionRemoteDataSourceImpl @Inject constructor(
    private val googleVisionApiService: GoogleVisionApiService,
    private val apiKeyProvider: ApiKeyProvider
) : GoogleVisionRemoteDataSource {

    override suspend fun analyzeImage(request: VisionRequestDto): VisionResponseDto =
        googleVisionApiService.analyzeImage(apiKeyProvider.getVisionApiKey(), request)
}
