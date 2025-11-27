package com.jg.childmomentsnap.feature.moment

import androidx.compose.runtime.Stable

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