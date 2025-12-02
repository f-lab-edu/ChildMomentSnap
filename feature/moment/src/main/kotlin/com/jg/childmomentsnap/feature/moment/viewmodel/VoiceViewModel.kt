package com.jg.childmomentsnap.feature.moment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.data.repository.PhotoRepository
import com.jg.childmomentsnap.feature.moment.PermissionState
import com.jg.childmomentsnap.feature.moment.VoiceUiState
import com.jg.childmomentsnap.feature.moment.model.CameraUiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 음성 녹음 기능용 ViewModel
 *
 * 음성 녹음 관련 상태 관리와 권한 처리를 담당
 */
@HiltViewModel
class VoiceViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CameraUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    /**
     * 음성 권한 상태를 업데이트
     */
    fun updateVoicePermissionState(hasVoicePermissions: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                voicePermissionState = if (hasVoicePermissions) {
                    PermissionState.Granted
                } else {
                    PermissionState.Denied
                }
            )
        }
    }

    /**
     * 음성 권한 요청 결과를 처리
     */
    fun onVoicePermissionResult(
        permissions: Map<String, Boolean>,
        hasAnyPermanentlyDenied: Boolean = false
    ) {
        val allGranted = permissions.values.all { it }

        _uiState.update { currentState ->
            currentState.copy(
                voicePermissionState = when {
                    allGranted -> {
                        PermissionState.Granted
                    }
                    hasAnyPermanentlyDenied -> {
                        PermissionState.PermanentlyDenied
                    }
                    else -> {
                        PermissionState.Denied
                    }
                }
            )
        }
    }

    /**
     * 음성 녹음 다이얼로그 표시
     */
    fun showVoiceRecordingDialog() {
        _uiState.update { currentState ->
            currentState.copy(showVoiceRecordingDialog = true)
        }
    }

    /**
     * 음성 녹음 다이얼로그 닫기
     */
    fun dismissVoiceRecordingDialog() {
        _uiState.update { currentState ->
            currentState.copy(showVoiceRecordingDialog = false)
        }
    }

    /**
     * 음성 녹음 사용을 확인하고 권한 요청 또는 녹음 시작
     */
    fun confirmVoiceRecording(imageBytes: ByteArray, onSuccess: () -> Unit) {
        // 음성 권한이 허용되어 있는지 확인
        if (_uiState.value.voicePermissionState == PermissionState.Granted) {
            // 권한이 있으면 녹음 완료 후 API 호출
            processImageWithVoice(imageBytes, onSuccess)
        } else {
            // 권한이 없으면 다이얼로그에서 권한 요청 처리
            // ConfirmStartRecordingDialog에서 권한 요청을 처리하므로 다이얼로그 유지
        }
    }

    /**
     * 음성 녹음 건너뛰기 - 기존 카메라 API 호출
     */
    fun skipVoiceRecording(imageBytes: ByteArray, onSuccess: () -> Unit) {
        // 음성 녹음 없이 기존 카메라 API 호출
        processImageWithVoice(imageBytes, onSuccess)
    }

    /**
     * 음성 녹음 완료 후 AI 분석 처리
     */
    private fun processImageWithVoice(imageBytes: ByteArray, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProcessingImage = true,
                    showVoiceRecordingDialog = false
                )
            }
            try {
                val analysis = photoRepository.analyzeImage(imageBytes)

                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false,
                        visionAnalysis = analysis
                    )
                }

                onSuccess()

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false
                    )
                }
                _uiEffect.emit(CameraUiEffect.ShowError("이미지를 처리하는 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    /**
     * 음성 녹음 상태 초기화
     */
    fun resetVoiceState() {
        _uiState.update { currentState ->
            currentState.copy(
                showVoiceRecordingDialog = false,
                isProcessingImage = false,
                visionAnalysis = null
            )
        }
    }
}