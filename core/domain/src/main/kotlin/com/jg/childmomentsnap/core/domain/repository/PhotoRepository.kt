package com.jg.childmomentsnap.core.domain.repository

import com.jg.childmomentsnap.core.model.VisionAnalysis

interface PhotoRepository {
    suspend fun analyzeImage(imageBytes: ByteArray): VisionAnalysis
}