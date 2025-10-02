package com.jg.childmomentsnap.core.data.repository

import android.content.Context
import android.net.Uri
import com.jg.childmomentsnap.core.common.util.ImageEncoder
import com.jg.childmomentsnap.core.data.model.network.request.AnalyticsImageDto
import com.jg.childmomentsnap.core.data.model.network.request.AnnotateImageRequest
import com.jg.childmomentsnap.core.data.model.network.request.Feature
import com.jg.childmomentsnap.core.data.model.network.request.FeatureType
import com.jg.childmomentsnap.core.data.model.network.request.Image
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PhotoRepository {
    override suspend fun analyzeImage(imageBytes: ByteArray): Flow<Unit> = flow {
        val base64Image = ImageEncoder.bytesToBase64(imageBytes)

        val request = AnalyticsImageDto(
            requests = listOf(
                AnnotateImageRequest(
                    image = Image(content = base64Image),
                    features = listOf(
                        Feature(type = FeatureType.FACE_DETECTION, maxResults = 10),
                        Feature(type = FeatureType.LABEL_DETECTION, maxResults = 10),
                        Feature(type = FeatureType.OBJECT_LOCALIZATION, maxResults = 10)
                    )
                )
            )
        )

        // TODO: RemoteDataSource를 통해 API 호출
        // val response = remoteDataSource.analyzeImage(request)

        // TODO: 응답 처리 로직

        emit(Unit)
    }

    override suspend fun analyzeImageFromUri(imageUri: Uri): Flow<Unit> = flow {
        val imageBytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
            ?: throw IllegalArgumentException("Cannot open input stream for URI: $imageUri")
        
        val base64Image = ImageEncoder.bytesToBase64(imageBytes)

        val request = AnalyticsImageDto(
            requests = listOf(
                AnnotateImageRequest(
                    image = Image(content = base64Image),
                    features = listOf(
                        Feature(type = FeatureType.FACE_DETECTION, maxResults = 10),
                        Feature(type = FeatureType.LABEL_DETECTION, maxResults = 10),
                        Feature(type = FeatureType.OBJECT_LOCALIZATION, maxResults = 10)
                    )
                )
            )
        )

        // TODO: RemoteDataSource를 통해 API 호출
        // val response = remoteDataSource.analyzeImage(request)

        // TODO: 응답 처리 로직

        emit(Unit)
    }
}