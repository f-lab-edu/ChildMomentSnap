package com.jg.childmomentsnap.core.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * 이미지 Base64 인코딩 유틸리티
 * Google Cloud Vision API 요청을 위한 이미지 변환 담당
 */
object ImageEncoder {

    /**
     * Uri를 Base64 문자열로 변환하는 헬퍼 함수입니다.
     * 이 함수는 Context 의존성을 가집니다.
     * @param context Android Context
     * @param uri 변환할 이미지 Uri
     * @return Base64 인코딩된 이미지 문자열
     */
    suspend fun uriToBase64(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 85
    ): String = withContext(Dispatchers.IO) {
        val imageBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")
        
        bytesToBase64(imageBytes, maxWidth, maxHeight, quality)
    }

    /**
     * ByteArray를 Base64 문자열로 변환합니다.
     * 이 함수는 안드로이드 Context 의존성이 없습니다.
     * @param imageBytes 변환할 이미지의 ByteArray
     * @return Base64 인코딩된 이미지 문자열
     */
    suspend fun bytesToBase64(
        imageBytes: ByteArray,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 85
    ): String = withContext(Dispatchers.IO) {
        try {
            // 원본 이미지 크기 확인
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

            // 샘플링 비율 계산
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false

            // 실제 이미지 로드
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
                ?: throw IllegalArgumentException("Cannot decode bitmap from byte array")

            // 추가 리사이징 (필요한 경우)
            val resizedBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                resizeBitmap(bitmap, maxWidth, maxHeight)
            } else {
                bitmap
            }

            // Base64 변환
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val resultBytes = outputStream.toByteArray()

            // 메모리 정리
            if (resizedBitmap != bitmap) {
                resizedBitmap.recycle()
            }
            bitmap.recycle()
            outputStream.close()

            Base64.encodeToString(resultBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            throw RuntimeException("Failed to convert bytes to Base64", e)
        }
    }

    /**
     * 이미지 샘플링 비율을 계산합니다 (메모리 효율성을 위해)
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * 비트맵을 지정된 크기로 리사이징합니다 (비율 유지)
     */
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val originalWidth = bitmap.width.toFloat()
        val originalHeight = bitmap.height.toFloat()

        val ratio = minOf(maxWidth / originalWidth, maxHeight / originalHeight)

        val targetWidth = (originalWidth * ratio).toInt()
        val targetHeight = (originalHeight * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    /**
     * Base64 문자열의 예상 크기를 계산합니다 (디버깅용)
     */
    fun calculateBase64Size(base64String: String): String {
        val sizeInBytes = (base64String.length * 3) / 4
        return when {
            sizeInBytes < 1024 -> "${sizeInBytes}B"
            sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024}KB"
            else -> "${sizeInBytes / (1024 * 1024)}MB"
        }
    }
}

/**
 * 이미지 압축 품질 프리셋
 */
object ImageQuality {
    const val HIGH = 95
    const val MEDIUM = 85
    const val LOW = 70
    const val VERY_LOW = 50
}

/**
 * 이미지 크기 프리셋
 */
object ImageSize {
    const val LARGE_WIDTH = 1920
    const val LARGE_HEIGHT = 1080

    const val MEDIUM_WIDTH = 1024
    const val MEDIUM_HEIGHT = 768

    const val SMALL_WIDTH = 640
    const val SMALL_HEIGHT = 480
}