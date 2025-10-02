package com.jg.childmomentsnap.core.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun analyzeImage(imageBytes: ByteArray): Flow<Unit>
    suspend fun analyzeImageFromUri(imageUri: Uri): Flow<Unit>
}