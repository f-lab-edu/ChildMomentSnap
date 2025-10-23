package com.jg.childmomentsnap.core.network.api

import com.jg.childmomentsnap.core.network.model.vision.VisionRequestDto
import com.jg.childmomentsnap.core.network.model.vision.VisionResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleVisionApiService {
    @POST("v1/images:annotate")
    suspend fun analyzeImage(
        @Query("key") apiKey: String,
        @Body request: VisionRequestDto
    ): VisionResponseDto
}
