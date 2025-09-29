package com.jg.childmomentsnap.feature.photo.components

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * CameraX를 사용하는 카메라 프리뷰 컴포저블
 * 
 * 이 컴포저블은 카메라 프리뷰 기능을 위해 CameraX를 통합합니다.
 * 카메라 생명주기, 렌즈 전환, 에러 상태를 처리합니다.
 * 
 * @param lensFacing 카메라 렌즈 방향 (전면/후면)
 * @param onCameraReady 카메라 준비 완료 콜백
 * @param onCameraError 카메라 에러 콜백
 * @param modifier 스타일링을 위한 Modifier
 */
@Composable
internal fun CameraPreview(
    lensFacing: Int,
    onCameraReady: () -> Unit,
    onCameraError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // 프리뷰와 프리뷰 뷰 생성
    val preview = remember { Preview.Builder().build() }
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
            
            // 카메라를 생명주기에 바인딩
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
            
            // 프리뷰를 서페이스에 연결
            preview.setSurfaceProvider(previewView.surfaceProvider)
            
            // 카메라 준비 완료 알림
            onCameraReady()
            
            Log.d("CameraPreview", "Camera bound successfully with lens facing: $lensFacing")
            
        } catch (e: Exception) {
            val errorMessage = "카메라 초기화 실패: ${e.message}"
            Log.e("CameraPreview", errorMessage, e)
            onCameraError(errorMessage)
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