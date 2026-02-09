package com.jg.childmomentsnap.feature.moment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.domain.usecase.ProcessMomentUseCase
import com.jg.childmomentsnap.core.model.VisionAnalysis
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
    private val processMomentUseCase: ProcessMomentUseCase,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceRecordingUiState())
    val uiState: StateFlow<VoiceRecordingUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<VoiceRecordingUiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private var imageBytes: ByteArray? = null

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

    fun setVisionAnalysisAndImageUri(visionAnalysisContent: String?, visionAnalysis: VisionAnalysis, uri: String) {
        _uiState.update { it.copy(visionAnalysisContent = visionAnalysisContent, visionAnalysis = visionAnalysis, imageUri = uri) }
    }

    fun updateVoicePermissionState(hasPermission: Boolean) {
        _uiState.update { it.copy(hasVoicePermission = hasPermission) }
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
                    amplitudes = emptyList(),
                    isProcessing = false
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

                voicePlayer.stopPlayback()
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
     * 녹음 완료 및 AI 처리 시작
     * TODO 녹음과 함꼐 처리 할 방안 추가 대응 필요
     */
    fun finishRecording() {
        viewModelScope.launch {
            val currentState = _uiState.value
            // 이미 녹음이 중지된 상태가 아니라면 먼저 중지 시도 (UI 흐름상 finishRecording은 완료 버튼 클릭 등에서 오므로)
            // 보통 stopRecording 후 사용자가 '완료' 버튼을 누름.
            // 여기서는 사용자가 '완료'를 눌렀을 때의 동작.
            
            // 만약 녹음 중이라면 중지
            if (currentState.recordingState == RecordingState.RECODING || currentState.recordingState == RecordingState.PAUSED) {
                 voiceRecorder.stopRecording()
            }
            voicePlayer.stopPlayback()

            val voiceFilePath = currentState.recordingFilePath
            val currentImageBytes = imageBytes

            if (voiceFilePath.isNullOrEmpty() || currentImageBytes == null) {
                _uiEffect.emit(
                    VoiceRecordingUiEffect.ShowErrorToast(
                        VoiceRecordingError.RECORDING_FILE_PATH_NOT_SET
                    )
                )
                 // Clean up and reset
                 cancelRecording()
                return@launch
            }
            
            _uiState.update { it.copy(isProcessing = true) }

            val voiceFile = File(voiceFilePath)
            
            val result = processMomentUseCase(currentImageBytes, voiceFile)
            
            result.onSuccess { momentData ->
                _uiEffect.emit(VoiceRecordingUiEffect.MomentAnalysisCompleted(momentData))
                // 성공 후 상태 초기화 또는 닫기는 UI에서 이펙트 처리 후 호출 또는 여기서 호출
                // 여기서는 처리 완료 후 상태만 업데이트하고, UI가 이펙트 받아 화면 이동 등을 수행하도록 함.
                 _uiState.update {
                    it.copy(
                        showVoiceRecordingBottomSheet = false,
                        recordingState = RecordingState.IDLE,
                        recordingDurationMs = 0L,
                        isPlayingRecording = false,
                        playbackPositionMs = 0L,
                        recordingFilePath = null,
                        amplitudes = emptyList(),
                        isProcessing = false
                    )
                }
            }.onFailure {
                 _uiState.update { it.copy(isProcessing = false) }
                 _uiEffect.emit(VoiceRecordingUiEffect.ShowErrorToast(VoiceRecordingError.RECORDING_STOP_FAILED)) // Or generic error
            }
        }
    }

    /**
     * BottomSheet가 닫힐 때 호출
     *  - 처리 중이거나 완료된 상태가 아니면 취소
     */
    fun onBottomSheetDismissed() {
        // 처리 중이라면 dismiss 막거나 취소 처리. 여기서는 취소 처리
        cancelRecording()
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
                        amplitudes = emptyList(),
                        isProcessing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        recordingState = RecordingState.IDLE,
                        recordingDurationMs = 0L,
                        isPlayingRecording = false,
                        playbackPositionMs = 0L,
                        amplitudes = emptyList(),
                        isProcessing = false
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
                    amplitudes = emptyList(),
                    isProcessing = false
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
