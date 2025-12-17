package com.jg.childmomentsnap.feature.moment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.data.repository.PhotoRepository
import com.jg.childmomentsnap.feature.moment.RecordingState
import com.jg.childmomentsnap.feature.moment.VoiceRecordingUiState
import com.jg.childmomentsnap.feature.moment.model.VoiceRecordingError
import com.jg.childmomentsnap.feature.moment.model.VoiceRecordingUiEffect
import com.jg.childmomentsnap.feature.moment.util.VoicePlayer
import com.jg.childmomentsnap.feature.moment.util.VoiceRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val MAX_AMPLITUDE_HISTORY = 40
private val MAX_MEDIA_RECORDER_AMPLITUDE = Short.MAX_VALUE.toFloat()

/**
 * 음성 녹음 기능 전용 ViewModel
 * 
 * 단일 책임 원칙에 따라 음성 녹음과 관련된 기능만을 담당합니다.
 * - 녹음 제어 (시작, 일시정지, 재개, 정지)
 * - 재생 제어
 * - 파일 관리
 * 
 * 주의: 권한 처리는 CameraViewModel에서 담당합니다.
 */
@HiltViewModel
class VoiceRecordingViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceRecordingUiState())
    val uiState: StateFlow<VoiceRecordingUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<VoiceRecordingUiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        observeRecordingData()
        observePlaybackPosition()
    }

    private fun observeRecordingData() {
        viewModelScope.launch {
            voiceRecorder.recordingData.collectLatest { data ->
                if (_uiState.value.recordingState == RecordingState.RECODING) {
                    _uiState.update { currentState ->
                        val normalizedAmplitude = normalizeAmplitude(data.maxAmplitude)
                        val updatedAmplitudes = appendAmplitude(
                            currentState.amplitudes,
                            normalizedAmplitude
                        )
                        currentState.copy(
                            recordingDurationMs = data.durationMs,
                            amplitudes = updatedAmplitudes
                        )
                    }
                }
            }
        }
    }

    private fun observePlaybackPosition() {
        viewModelScope.launch {
            voicePlayer.playbackPosition.collectLatest { position ->
                if (_uiState.value.isPlayingRecording) {
                    _uiState.update { it.copy(playbackPositionMs = position) }
                }
            }
        }
    }

    fun setVoiceRecordingFilePath(filePath: String) {
        _uiState.update {
            it.copy(recordingFilePath = filePath)
        }
    }

    /**
     * 음성 녹음 버텀시트 표시
     */
    fun showVoiceRecordingBottomSheet() {
        viewModelScope.launch {
            _uiState.update { it.copy(showVoiceRecordingBottomSheet = true) }
        }
    }

    /**
     * 음성 녹음 버텀시트 닫기
     */
    fun dismissVoiceRecordingBottomSheet() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    showVoiceRecordingBottomSheet = false,
                    recordingState = RecordingState.IDLE,
                    recordingDurationMs = 0L,
                    isPlayingRecording = false,
                    playbackPositionMs = 0L,
                    amplitudes = emptyList()
                ) 
            }
        }
    }

    /**
     * 녹음 시작
     */
    fun startRecording() {
        viewModelScope.launch {
            try {
                val filePath = _uiState.value.recordingFilePath
                if (filePath.isNullOrEmpty()) {
                    _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.RECORDING_FILE_PATH_NOT_SET))
                    return@launch
                }

                voiceRecorder.startRecording(filePath, viewModelScope)

                _uiState.update { currentState ->
                    currentState.copy(
                        recordingState = RecordingState.RECODING,
                        recordingDurationMs = 0L,
                        amplitudes = emptyList()
                    )
                }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(recordingState = RecordingState.IDLE) 
                }
                _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.RECORDING_START_FAILED))
            }
        }
    }

    /**
     * 녹음 일시정지
     */
    fun pauseRecording() {
        viewModelScope.launch {
            try {
                voiceRecorder.pauseRecording()
                _uiState.update { currentState ->
                    currentState.copy(recordingState = RecordingState.PAUSED)
                }
            } catch (e: Exception) {
                _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.RECORDING_PAUSE_FAILED))
            }
        }
    }

    /**
     * 녹음 재개
     */
    fun resumeRecording() {
        viewModelScope.launch {
            try {
                voiceRecorder.resumeRecording(viewModelScope)
                _uiState.update { currentState ->
                    currentState.copy(recordingState = RecordingState.RECODING)
                }
            } catch (e: Exception) {
                _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.RECORDING_RESUME_FAILED))
            }
        }
    }

    /**
     * 녹음 정지
     */
    fun stopRecording() {
        viewModelScope.launch {
            try {
                voiceRecorder.stopRecording()
                
                _uiState.update { currentState ->
                    currentState.copy(recordingState = RecordingState.STOPPED)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(recordingState = RecordingState.STOPPED)
                }
                _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.RECORDING_STOP_FAILED))
            }
        }
    }

    /**
     * 녹음 파일 재생
     */
    fun playRecording() {
        viewModelScope.launch {
            try {
                val filePath = _uiState.value.recordingFilePath
                if (filePath.isNullOrEmpty()) {
                    _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.NO_RECORDING_FILE_TO_PLAY))
                    return@launch
                }

                voicePlayer.play(
                    filePath = filePath,
                    scope = viewModelScope,
                    onCompletion = {
                         viewModelScope.launch {
                             stopPlayback()
                         }
                    },
                    onError = {
                        viewModelScope.launch {
                            _uiState.update { 
                                it.copy(isPlayingRecording = false) 
                            }
                            _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.PLAYBACK_ERROR))
                        }
                    }
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        isPlayingRecording = true,
                        playbackPositionMs = 0L,
                        recordingState = RecordingState.ON_PLAYING
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isPlayingRecording = false)
                }
                _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.PLAYBACK_START_FAILED))
            }
        }
    }

    /**
     * 녹음 파일 재생 정지
     */
    fun stopPlayback() {
        viewModelScope.launch {
            try {
                voicePlayer.stopPlayback()
                
                _uiState.update { currentState ->
                    currentState.copy(
                        isPlayingRecording = false,
                        playbackPositionMs = 0L,
                        recordingState = RecordingState.STOPPED
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isPlayingRecording = false,
                        playbackPositionMs = 0L,
                        recordingState = RecordingState.STOPPED
                    )
                }
                _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.PLAYBACK_STOP_FAILED))
            }
        }
    }

    /**
     * 녹음 시간 업데이트 (실시간)
     */
    fun updateRecordingDuration(durationMs: Long) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(recordingDurationMs = durationMs)
            }
        }
    }

    /**
     * 재생 위치 업데이트 (실시간)
     */
    fun updatePlaybackPosition(positionMs: Long) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(playbackPositionMs = positionMs)
            }
        }
    }

    private fun normalizeAmplitude(rawAmplitude: Int): Float {
        if (rawAmplitude <= 0) return 0f
        return (rawAmplitude / MAX_MEDIA_RECORDER_AMPLITUDE).coerceIn(0f, 1f)
    }

    private fun appendAmplitude(
        amplitudes: List<Float>,
        newAmplitude: Float
    ): List<Float> {
        val updated = amplitudes + newAmplitude
        return if (updated.size > MAX_AMPLITUDE_HISTORY) {
            updated.takeLast(MAX_AMPLITUDE_HISTORY)
        } else {
            updated
        }
    }


    /**
     * 녹음 완료 후 이벤트 발생
     */
    fun finishRecording() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.recordingState != RecordingState.STOPPED) {
                cancelRecording()
                return@launch
            }

            val voiceFilePath = currentState.recordingFilePath
            if (voiceFilePath.isNullOrEmpty()) {
                _uiEffect.emit(
                    VoiceRecordingUiEffect.ShowErrorToast(
                        VoiceRecordingError.RECORDING_FILE_PATH_NOT_SET
                    )
                )
                _uiState.update {
                    it.copy(
                        showVoiceRecordingBottomSheet = false,
                        recordingState = RecordingState.IDLE,
                        recordingDurationMs = 0L,
                        isPlayingRecording = false,
                        playbackPositionMs = 0L,
                        recordingFilePath = null,
                        amplitudes = emptyList()
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    showVoiceRecordingBottomSheet = false,
                    recordingState = RecordingState.IDLE,
                    recordingDurationMs = 0L,
                    isPlayingRecording = false,
                    playbackPositionMs = 0L,
                    recordingFilePath = null,
                    amplitudes = emptyList()
                )
            }
            _uiEffect.emit(VoiceRecordingUiEffect.NotifyRecordingCompleted(voiceFilePath))
        }
    }

    /**
     * BottomSheet가 닫힐 때 호출
     *  - 녹음이 완료된 상태라면 완료 이벤트 발생
     *  - 그 외 상태라면 취소로 간주
     */
    fun onBottomSheetDismissed() {
        if (_uiState.value.recordingState == RecordingState.STOPPED) {
            finishRecording()
        } else {
            cancelRecording()
        }
    }

    /**
     * 녹음 리셋 (재시작을 위해 모든 내용 초기화)
     */
    fun resetRecording() {
        viewModelScope.launch {
            try {
                voiceRecorder.stopRecording()
                voicePlayer.stopPlayback()
                
                // 녹음 파일 삭제
                _uiState.value.recordingFilePath?.let { filePath ->
                    val file = File(filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                }
                
                // UI 상태 초기화
                _uiState.update {
                    it.copy(
                        recordingState = RecordingState.IDLE,
                        recordingDurationMs = 0L,
                        isPlayingRecording = false,
                        playbackPositionMs = 0L,
                        amplitudes = emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        recordingState = RecordingState.IDLE,
                        recordingDurationMs = 0L,
                        isPlayingRecording = false,
                        playbackPositionMs = 0L,
                        amplitudes = emptyList()
                    )
                }
                _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.RESET_FAILED))
            }
        }
    }

    /**
     * 녹음 취소
     */
    fun cancelRecording() {
        viewModelScope.launch {
            // 진행 중인 모든 작업 정리
            try {
                voiceRecorder.stopRecording()
                voicePlayer.stopPlayback()
                
                // 녹음 파일 삭제
                _uiState.value.recordingFilePath?.let { filePath ->
                    val file = File(filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            } catch (e: Exception) {
                // 정리 중 오류는 무시하고 UI만 초기화
            }
            
            _uiState.update { 
                it.copy(
                    showVoiceRecordingBottomSheet = false,
                    recordingState = RecordingState.IDLE,
                    recordingDurationMs = 0L,
                    recordingFilePath = null,
                    isPlayingRecording = false,
                    playbackPositionMs = 0L,
                    amplitudes = emptyList()
                ) 
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceRecorder.stopRecording()
        voicePlayer.stopPlayback()
    }
}
