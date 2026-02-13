package com.jg.childmomentsnap.feature.moment

import androidx.compose.runtime.Stable
import com.jg.childmomentsnap.core.model.VisionAnalysis

/**
 * 음성 녹음 기능의 UI 상태
 * 
 * 순수하게 녹음 기능만 관리합니다.
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
    val isProcessing: Boolean = false,
    val imageUri: String? = null,
    val visionAnalysis: VisionAnalysis? = null,
    val editedContent: String? = null,
    val emotionChips: List<Int> = emptyList()
) {
    /**
     * RecordingControlsState로 변환하는 편의 함수
     */
    fun toRecordingControlsState(): RecordingControlsState {
        return RecordingControlsState.fromRecordingState(recordingState)
    }
}

