package com.jg.childmomentsnap.core.data.repository

import com.jg.childmomentsnap.core.model.VisionAnalysis

interface PhotoRepository {
    suspend fun analyzeImage(imageBytes: ByteArray): VisionAnalysis
}
