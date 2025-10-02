package com.jg.childmomentsnap.core.data.repository

import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun analyzeImage(imageBytes: ByteArray): Flow<Unit>
}