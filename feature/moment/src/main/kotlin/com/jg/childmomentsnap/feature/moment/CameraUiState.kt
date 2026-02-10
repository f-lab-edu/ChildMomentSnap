package com.jg.childmomentsnap.feature.moment

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.Stable
import com.jg.childmomentsnap.core.model.VisionAnalysis

/**
 * 카메라 화면의 UI 상태
 * 
 * 권한 상태, 카메라 상태, 사용자 상호작용을 포함한
 * 카메라 기능에 필요한 모든 상태를 나타냅니다.
 */
data class CameraUiState(
    val cameraPermissionState: PermissionState = PermissionState.Checking,
    val voicePermissionState: PermissionState = PermissionState.Checking,
    val cameraState: CameraState = CameraState.Idle,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val isCapturing: Boolean = false,
    val shouldCapture: Boolean = false,
    val capturedImageUri: Uri? = null,
    val selectedImageUri: Uri? = null,
    val showGalleryPicker: Boolean = false,
    val showCapturedImageDialog: Boolean = false,
    val showVoiceRecordingDialog: Boolean = false,
    val isProcessingImage: Boolean = false,
    val visionAnalysis: VisionAnalysis? = null,
    val visionAnalysisContent: String? = null,
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

/**
 * 녹음 상태를 나타냅니다
 */
enum class RecordingState {
    /** 녹음 대기 상태 */
    IDLE,
    /** 녹음 중 */
    RECODING,
    /** 녹음 일시 정지 */
    PAUSED,
    /** 녹음 완료 */
    STOPPED,
    /** 플레이 */
    ON_PLAYING
}

/**
 * 녹음 컨트롤의 상태를 나타냅니다
 */
@Stable
data class RecordingControlsState(
    val canStartRecording: Boolean,
    val canPauseRecording: Boolean,
    val canResumeRecording: Boolean,
    val canStopRecording: Boolean,
    val canPlayRecording: Boolean,
    val canResetRecording: Boolean,
    val isRecording: Boolean,
    val isPlaying: Boolean,
    val isPaused: Boolean,
    val isStopped: Boolean
) {
    companion object {
        fun fromRecordingState(recordingState: RecordingState): RecordingControlsState {
            return when (recordingState) {
                RecordingState.IDLE -> RecordingControlsState(
                    canStartRecording = true,
                    canPauseRecording = false,
                    canResumeRecording = false,
                    canStopRecording = false,
                    canPlayRecording = false,
                    canResetRecording = false,
                    isRecording = false,
                    isPlaying = false,
                    isPaused = false,
                    isStopped = false
                )
                RecordingState.RECODING -> RecordingControlsState(
                    canStartRecording = false,
                    canPauseRecording = true,
                    canResumeRecording = false,
                    canStopRecording = true,
                    canPlayRecording = false,
                    canResetRecording = true,
                    isRecording = true,
                    isPlaying = false,
                    isPaused = false,
                    isStopped = false
                )
                RecordingState.PAUSED -> RecordingControlsState(
                    canStartRecording = false,
                    canPauseRecording = false,
                    canResumeRecording = true,
                    canStopRecording = true,
                    canPlayRecording = false,
                    canResetRecording = true,
                    isRecording = false,
                    isPlaying = false,
                    isPaused = true,
                    isStopped = false
                )
                RecordingState.STOPPED -> RecordingControlsState(
                    canStartRecording = false,
                    canPauseRecording = false,
                    canResumeRecording = false,
                    canStopRecording = false,
                    canPlayRecording = true,
                    canResetRecording = true,
                    isRecording = false,
                    isPlaying = false,
                    isPaused = false,
                    isStopped = true
                )
                RecordingState.ON_PLAYING -> RecordingControlsState(
                    canStartRecording = false,
                    canPauseRecording = false,
                    canResumeRecording = false,
                    canStopRecording = false,
                    canPlayRecording = false,
                    canResetRecording = true,
                    isRecording = false,
                    isPlaying = true,
                    isPaused = false,
                    isStopped = false
                )
            }
        }
    }
}
