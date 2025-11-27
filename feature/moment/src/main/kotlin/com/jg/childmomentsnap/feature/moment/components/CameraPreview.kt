package com.jg.childmomentsnap.feature.moment.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * CameraX를 사용하는 카메라 프리뷰 컴포저블
 * 
 * 이 컴포저블은 카메라 프리뷰와 사진 촬영 기능을 위해 CameraX를 통합합니다.
 * 카메라 생명주기, 렌즈 전환, 에러 상태를 처리합니다.
 * 
 * @param lensFacing 카메라 렌즈 방향 (전면/후면)
 * @param shouldCapture 사진 촬영 트리거 (true일 때 촬영 실행)
 * @param onCameraReady 카메라 준비 완료 콜백
 * @param onCameraError 카메라 에러 콜백
 * @param onPhotoCaptured 사진 촬영 완료 콜백 (촬영된 이미지 URI 전달)
 * @param onCaptureComplete 촬영 프로세스 완료 콜백 (트리거 리셋용)
 * @param modifier 스타일링을 위한 Modifier
 */
@Composable
internal fun CameraPreview(
    lensFacing: Int,
    shouldCapture: Boolean,
    onCameraReady: () -> Unit,
    onCameraError: (String) -> Unit,
    onPhotoCaptured: (Uri) -> Unit,
    onCaptureComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // 프리뷰와 이미지 캡처 생성
    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember { 
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build() 
    }
    val previewView = remember { PreviewView(context) }
    
    // 렌즈 방향에 따른 카메라 선택자 생성
    val cameraSelector = remember(lensFacing) {
        CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
    }

    // 렌즈 방향 변경 시 카메라 바인딩 처리
    LaunchedEffect(lensFacing) {
        try {
            val cameraProvider = context.getCameraProvider()
            
            // 이전 바인딩 모두 해제
            cameraProvider.unbindAll()
            
            // 카메라를 생명주기에 바인딩 (프리뷰 + 이미지 캡처)
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            
            // 프리뷰를 서페이스에 연결
            preview.surfaceProvider = previewView.surfaceProvider
            
            // 카메라 준비 완료 알림
            onCameraReady()
        } catch (e: Exception) {
            val errorMessage = "카메라 초기화 실패: ${e.message}"
            onCameraError(errorMessage)
        }
    }

    // 사진 촬영 트리거 처리
    LaunchedEffect(shouldCapture) {
        if (shouldCapture) {
            try {
                capturePhoto(context, imageCapture, onPhotoCaptured)
            } catch (e: Exception) {
                onCameraError("사진 촬영 실패: ${e.message}")
            } finally {
                onCaptureComplete()
            }
        }
    }

    // 카메라 프리뷰를 표시하는 AndroidView
    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

/**
 * CameraProvider를 가져오는 확장 함수
 * 
 * CameraProvider가 사용 가능해질 때까지 대기하여
 * 콜백 지옥 없이 코루틴에서 사용하기 쉽게 만듭니다.
 */
private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener({
                try {
                    continuation.resume(future.get())
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Failed to get camera provider", e)
                    throw e
                }
            }, ContextCompat.getMainExecutor(this))
        }
    }

/**
 * 실제 사진 촬영을 수행하는 함수
 * 
 * @param context Android Context
 * @param imageCapture ImageCapture 인스턴스
 * @param onPhotoCaptured 촬영 완료 콜백
 */
private suspend fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoCaptured: (Uri) -> Unit
) = suspendCoroutine<Unit> { continuation ->
    // 촬영할 이미지 파일 생성
    val photoFile = File(
        context.getExternalFilesDir(null),
        "captured_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())}.jpg"
    )
    
    // 촬영 옵션 설정
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
    // 사진 촬영 실행
    imageCapture.takePicture(
        outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onPhotoCaptured(savedUri)
                continuation.resume(Unit)
            }
            
            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPreview", "Photo capture failed", exception)
                continuation.resumeWith(Result.failure(exception))
            }
        }
    )
}