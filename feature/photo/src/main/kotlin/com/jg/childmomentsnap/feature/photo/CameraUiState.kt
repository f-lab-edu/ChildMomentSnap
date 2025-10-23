package com.jg.childmomentsnap.feature.photo

import android.net.Uri
import androidx.camera.core.CameraSelector
import com.jg.childmomentsnap.core.model.VisionAnalysis

/**
 * 카메라 화면의 UI 상태
 * 
 * 권한 상태, 카메라 상태, 사용자 상호작용을 포함한
 * 카메라 기능에 필요한 모든 상태를 나타냅니다.
 */
data class CameraUiState(
    val permissionState: PermissionState = PermissionState.Checking,
    val cameraState: CameraState = CameraState.Idle,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val isCapturing: Boolean = false,
    val shouldCapture: Boolean = false,
    val capturedImageUri: Uri? = null,
    val selectedImageUri: Uri? = null,
    val showGalleryPicker: Boolean = false,
    val showCapturedImageDialog: Boolean = false,
    val isProcessingImage: Boolean = false,
    val visionAnalysis: VisionAnalysis? = null,
    val errorMessage: String? = null
)

/**
 * 카메라 권한의 현재 상태를 나타냅니다
 */
enum class PermissionState {
    /** 권한 확인 중 */
    Checking,
    /** 필요한 모든 권한이 허용됨 */
    Granted,
    /** 일부 권한이 거부되었지만 다시 요청 가능 */
    Denied,
    /** 권한이 영구적으로 거부됨, 사용자가 설정에서 직접 허용해야 함 */
    PermanentlyDenied
}

/**
 * 카메라의 현재 상태를 나타냅니다
 */
enum class CameraState {
    /** 카메라가 초기화되지 않음 */
    Idle,
    /** 카메라가 준비되어 프리뷰를 표시 중 */
    Previewing,
    /** 카메라가 사진을 촬영하는 중 */
    Capturing,
    /** 카메라에서 오류가 발생함 */
    Error
}
