package com.jg.childmomentsnap.feature.moment

import androidx.compose.runtime.Stable

/**
 * 음성 녹음 기능의 UI 상태
 * 
 * 순수하게 녹음 기능만 관리합니다.
 * 권한은 CameraViewModel에서 관리하므로 제외됩니다.
 */
@Stable
data class VoiceRecordingUiState(
    val showVoiceRecordingBottomSheet: Boolean = false,
    val recordingState: RecordingState = RecordingState.IDLE,
    val recordingDurationMs: Long = 0L,
    val recordingFilePath: String? = null,
    val isPlayingRecording: Boolean = false,
    val playbackPositionMs: Long = 0L,
    val amplitudes: List<Float> = emptyList(),
    val isProcessing: Boolean = false
) {
    /**
     * RecordingControlsState로 변환하는 편의 함수
     */
    fun toRecordingControlsState(): RecordingControlsState {
        return RecordingControlsState.fromRecordingState(recordingState)
    }
}
