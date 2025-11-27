package com.jg.childmomentsnap.feature.moment.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jg.childmomentsnap.feature.moment.CameraUiState
import com.jg.childmomentsnap.feature.moment.PermissionState
import com.jg.childmomentsnap.feature.moment.components.CapturedImagePreview
import com.jg.childmomentsnap.feature.moment.components.PhotoPicker
import com.jg.childmomentsnap.feature.moment.components.SelectedImagePreview
import com.jg.childmomentsnap.feature.moment.components.ConfirmStartRecordingDialog
import components.CameraLoadingScreen
import components.CameraPermissionPermanentlyDeniedScreen
import components.CameraPermissionScreen
import components.CameraPreviewContainer


/**
 * 카메라 라우트 플로우
 *  1. Camera Route에서 Camera, 저장소 권한 체크
 *      1-1. 권한
 *           YES -> 카메라 뷰
 *           NO -> 권한 요청
 *
 *  2. 카메라 뷰 실행
 *  3. 사진 촬영
 *  4. 사진 저장
 *  5. 사진 경로 AI API 호출
 *  6. AI API 결과를 받으면 글쓰기 화면으로 데이터 전달
 */
/**
 * 상태 기반 조건부 렌더링을 하는 메인 카메라 화면
 * 
 * 이 컴포저블은 권한과 카메라 상태에 따른 조건부 렌더링을 통해
 * 메인 카메라 화면 로직을 처리합니다. 부모로부터 모든 상태를 받고
 * 콜백을 통해 이벤트를 노출하는 State Hoisting 패턴을 따릅니다.
 * 
 * @param uiState 현재 UI 상태
 * @param onPermissionResult 권한 요청 결과 콜백
 * @param onCameraReady 카메라 준비 완료 콜백
 * @param onSwitchCamera 카메라 전환 콜백
 * @param onCapturePhoto 사진 촬영 콜백
 * @param onGalleryClick 갤러리 접근 콜백
 * @param onImageSelected 갤러리에서 이미지 선택 콜백
 * @param onDismissGalleryPicker 갤러리 선택기 닫기 콜백
 * @param onConfirmImage 선택된 이미지 사용 확인 콜백
 * @param onCancelImage 선택된 이미지 취소 콜백
 * @param onClearError 에러 제거 콜백
 * @param onCameraError 카메라 에러 콜백
 * @param onNavigateUp 뒤로 이동 콜백
 * @param modifier 스타일링을 위한 Modifier
 */
@Composable
internal fun CameraScreen(
    uiState: CameraUiState,
    onPermissionResult: (Map<String, Boolean>, Boolean) -> Unit,
    onVoicePermissionResult: (Map<String, Boolean>, Boolean) -> Unit,
    onCameraReady: () -> Unit,
    onSwitchCamera: () -> Unit,
    onCapturePhoto: () -> Unit,
    onGalleryClick: () -> Unit,
    onImageSelected: (android.net.Uri?) -> Unit,
    onDismissGalleryPicker: () -> Unit,
    onConfirmImage: () -> Unit,
    onCancelImage: () -> Unit,
    onPhotoCaptured: (android.net.Uri) -> Unit,
    onCaptureComplete: () -> Unit,
    onConfirmCapturedImage: () -> Unit,
    onRetakeCapturedPhoto: () -> Unit,
    onConfirmVoiceRecording: () -> Unit,
    onSkipVoiceRecording: () -> Unit,
    onDismissVoiceRecordingDialog: () -> Unit,
    onClearError: () -> Unit,
    onCameraError: (String) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

    // 권한 상태에 따른 조건부 렌더링
    when (uiState.cameraPermissionState) {
        PermissionState.Checking -> {
            CameraLoadingScreen(
                onNavigateUp = onNavigateUp,
                modifier = modifier
            )
        }
        
        PermissionState.Denied -> {
            CameraPermissionScreen(
                onPermissionResult = onPermissionResult,
                onNavigateUp = onNavigateUp,
                modifier = modifier
            )
        }
        
        PermissionState.Granted -> {
            CameraPreviewContainer(
                uiState = uiState,
                onCameraReady = onCameraReady,
                onSwitchCamera = onSwitchCamera,
                onCapturePhoto = onCapturePhoto,
                onGalleryClick = onGalleryClick,
                onCameraError = onCameraError,
                onPhotoCaptured = onPhotoCaptured,
                onCaptureComplete = onCaptureComplete,
                onNavigateUp = onNavigateUp,
                modifier = modifier
            )
        }
        
        PermissionState.PermanentlyDenied -> {
            CameraPermissionPermanentlyDeniedScreen(
                onNavigateUp = onNavigateUp,
                modifier = modifier
            )
        }
    }
    
    // 선택된 이미지 미리보기
    uiState.selectedImageUri?.let { imageUri ->
        SelectedImagePreview(
            imageUri = imageUri,
            isProcessing = uiState.isProcessingImage,
            onConfirm = onConfirmImage,
            onCancel = onCancelImage,
            onNavigateUp = onNavigateUp,
            modifier = modifier
        )
    }
    
    // 촬영된 사진 확인 다이얼로그
    uiState.capturedImageUri?.let { imageUri ->
        if (uiState.showCapturedImageDialog) {
            CapturedImagePreview(
                imageUri = imageUri,
                isProcessing = uiState.isProcessingImage,
                onConfirm = onConfirmCapturedImage,
                onRetake = onRetakeCapturedPhoto,
                onCancel = onRetakeCapturedPhoto,
                modifier = modifier
            )
        }
    }
    
    // 갤러리 선택기
    PhotoPicker(
        show = uiState.showGalleryPicker,
        onImageSelected = onImageSelected,
        onDismiss = onDismissGalleryPicker
    )
    
    // 음성 녹음 다이얼로그
    if (uiState.showVoiceRecordingDialog) {
        ConfirmStartRecordingDialog(
            voicePermissionState = uiState.voicePermissionState,
            onVoicePermissionResult = onVoicePermissionResult,
            onConfirmVoiceRecording = onConfirmVoiceRecording,
            onSkipVoiceRecording = onSkipVoiceRecording,
            onDismiss = onDismissVoiceRecordingDialog,
            modifier = modifier
        )
    }
}