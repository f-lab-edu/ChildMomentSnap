package com.jg.childmomentsnap.feature.moment

import androidx.compose.runtime.Stable
import com.jg.childmomentsnap.core.model.VisionAnalysis

@Stable
data class RecordingControlsState(
    val canStartRecording: Boolean,
    val canPauseRecording: Boolean,
    val canResumeRecording: Boolean,
    val canStopRecording: Boolean,
    val isRecording: Boolean,
    val isPlaying: Boolean,
    val isPaused: Boolean
)

enum class RecordingState {
    IDLE,
    RECODING,
    PAUSED,
    STOPPED
}

/**
 * 음성 녹음 기능의 UI 상태
 */
@Stable
data class VoiceUiState(
    val voicePermissionState: PermissionState = PermissionState.Checking,
    val showVoiceRecordingDialog: Boolean = false,
    val isProcessingImage: Boolean = false,
    val visionAnalysis: VisionAnalysis? = null
)