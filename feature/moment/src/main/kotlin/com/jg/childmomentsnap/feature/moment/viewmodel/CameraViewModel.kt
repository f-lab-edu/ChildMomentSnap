package com.jg.childmomentsnap.feature.moment.viewmodel

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.domain.repository.PhotoRepository
import com.jg.childmomentsnap.feature.moment.CameraState
import com.jg.childmomentsnap.feature.moment.CameraUiState
import com.jg.childmomentsnap.feature.moment.PermissionState
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
 * 카메라 화면용 ViewModel
 *
 * Android 의존성 없이 카메라 관련 모든 상태를 관리
 */
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CameraUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()


    /**
     * 카메라와 음성 권한 상태를 업데이트
     */
    fun updatePermissionState(hasAllCameraPermissions: Boolean, hasAllVoicePermissions: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                cameraPermissionState = if (hasAllCameraPermissions) {
                    PermissionState.Granted
                } else {
                    PermissionState.Denied
                },
                voicePermissionState = if (hasAllVoicePermissions) {
                    PermissionState.Granted
                } else {
                    PermissionState.Denied
                }
            )
        }
    }

    /**
     * 카메라 권한 상태를 업데이트
     */
    fun updateCameraPermissionState(hasAllCameraPermissions: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                cameraPermissionState = if (hasAllCameraPermissions) {
                    PermissionState.Granted
                } else {
                    PermissionState.Denied
                }
            )
        }
    }

    /**
     * 음성 권한 상태를 업데이트
     */
    fun updateVoicePermissionState(hasAllVoicePermissions: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                voicePermissionState = if (hasAllVoicePermissions) {
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
                    allGranted -> PermissionState.Granted
                    hasAnyPermanentlyDenied -> PermissionState.PermanentlyDenied
                    else -> PermissionState.Denied
                }
            )
        }
    }

    /**
     * ActivityResult에서 권한 요청 결과를 처리
     * 권한이 영구적으로 거부되었는지 판단
     */
    fun onPermissionResult(
        permissions: Map<String, Boolean>,
        hasAnyPermanentlyDenied: Boolean = false
    ) {
        val allGranted = permissions.values.all { it }

        _uiState.update { currentState ->
            currentState.copy(
                cameraPermissionState = when {
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
     * 카메라 프리뷰가 성공적으로 초기화되었을 때 호출
     */
    fun onCameraReady() {
        _uiState.update { currentState ->
            currentState.copy(cameraState = CameraState.Previewing)
        }
    }

    /**
     * 전면과 후면 카메라를 전환
     */
    fun switchCamera() {
        _uiState.update { currentState ->
            currentState.copy(
                lensFacing = if (currentState.lensFacing == CameraSelector.LENS_FACING_BACK) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }
            )
        }
    }

    /**
     * 사진 촬영 트리거를 설정 (CameraPreview에서 실제 촬영 수행)
     */
    fun capturePhoto() {
        _uiState.update { currentState ->
            currentState.copy(
                isCapturing = true,
                shouldCapture = true
            )
        }
    }

    /**
     * CameraPreview에서 사진 촬영이 완료되었을 때 호출
     */
    fun onPhotoCaptured(uri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                isCapturing = false,
                capturedImageUri = uri,
                showCapturedImageDialog = true,
                cameraState = CameraState.Previewing
            )
        }
    }

    /**
     * 사진 촬영 프로세스가 완료되었을 때 트리거를 리셋
     */
    fun onCaptureComplete() {
        _uiState.update { currentState ->
            currentState.copy(
                shouldCapture = false,
                isCapturing = false
            )
        }
    }

    /**
     * 현재 에러 메시지를 클리어
     */
    fun clearError() {
        viewModelScope.launch {
            _uiEffect.emit(CameraUiEffect.ShowError(""))
        }
    }

    /**
     * 카메라 초기화 또는 사용 중 오류를 처리
     */
    fun onCameraError(error: String) {
        _uiState.update { currentState ->
            currentState.copy(
                cameraState = CameraState.Error,
            )
        }

        viewModelScope.launch {
            _uiEffect.emit(CameraUiEffect.ShowError(error))
        }
    }

    /**
     * 갤러리를 열어 이미지를 선택
     */
    fun openGallery() {
        _uiState.update { currentState ->
            currentState.copy(showGalleryPicker = true)
        }
    }

    /**
     * 갤러리에서 이미지를 선택한 후 처리
     */
    fun onImageSelected(uri: Uri?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedImageUri = uri,
                showGalleryPicker = false
            )
        }
    }

    /**
     * 선택된 이미지 사용을 확인하고 AI 처리를 시작
     */
    fun confirmSelectedImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true, visionAnalysis = null) }
            try {
                val analysis = photoRepository.analyzeImage(imageBytes)

                // 성공 시, 결과 화면으로 이동하거나 상태를 업데이트 합니다.
                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false,
                        visionAnalysis = analysis
                    )
                }

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
     * 선택된 이미지를 취소하고 카메라로 돌아감
     */
    fun cancelSelectedImage() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedImageUri = null,
                isProcessingImage = false
            )
        }
    }

    /**
     * 갤러리 선택기를 닫기
     */
    fun dismissGalleryPicker() {
        _uiState.update { currentState ->
            currentState.copy(showGalleryPicker = false)
        }
    }

    /**
     * 촬영된 이미지에 대한 AI 분석 시작
     */
    fun startAnalysis(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true, visionAnalysis = null) }
            try {
                val analysis = photoRepository.analyzeImage(imageBytes)

                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false,
                        visionAnalysis = analysis
                    )
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false
                    )
                }
                _uiEffect.emit(CameraUiEffect.ShowError("이미지를 분석하는 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    /**
     * 촬영된 사진 사용을 확인
     * (기존 다이얼로그 로직은 유지하되, 새로운 흐름에서는 startAnalysis를 직접 호출하여 사용됨)
     */
    fun confirmCapturedImage() {
        _uiState.update { currentState ->
            if (currentState.showVoiceRecordingDialog) {
                currentState
            } else {
                currentState.copy(showVoiceRecordingDialog = true)
            }
        }
    }

    /**
     * 촬영된 사진을 취소하고 다시 촬영
     */
    fun retakeCapturedPhoto() {
        _uiState.update { currentState ->
            currentState.copy(
                capturedImageUri = null,
                showCapturedImageDialog = false,
                visionAnalysis = null, // 분석 결과 초기화
                showVoiceRecordingDialog = false
            )
        }
    }

    // ... (rest of the file)



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
     * 음성 녹음을 확인하고 BottomSheet 표시
     */
    fun confirmVoiceRecording(imageBytes: ByteArray) {
        // 음성 권한이 허용되어 있는지 확인
        if (_uiState.value.voicePermissionState == PermissionState.Granted) {
            // 권한이 있으면 다이얼로그 닫고 BottomSheet 표시는 VoiceRecordingViewModel에서 처리
            _uiState.update { currentState ->
                currentState.copy(showVoiceRecordingDialog = false)
            }
            // TODO: VoiceRecordingViewModel의 showVoiceRecordingBottomSheet 호출 필요
        } else {
            // 권한이 없으면 다이얼로그에서 권한 요청 처리
            // ConfirmStartRecordingDialog에서 권한 요청을 처리하므로 다이얼로그 유지
        }
    }

    /**
     * 음성 녹음 건너뛰기 - 이미지만으로 AI API 호출
     */
    fun skipVoiceRecording(imageBytes: ByteArray) {
        _uiState.update { currentState ->
            currentState.copy(
                showVoiceRecordingDialog = false,
                showCapturedImageDialog = false,
                capturedImageUri = null
            )
        }
        processImageWithoutVoice(imageBytes)
    }

    /**
     * 음성 녹음 완료 후 콜백 처리
     */
    fun onVoiceRecordingCompleted(imageBytes: ByteArray, voiceFilePath: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                showCapturedImageDialog = false,
                capturedImageUri = null
            )
        }
        processImageWithVoice(imageBytes, voiceFilePath)
    }

    /**
     * 음성과 함께 이미지 처리 (AI API 호출)
     */
    private fun processImageWithVoice(imageBytes: ByteArray, voiceFilePath: String?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isProcessingImage = true)
            }
            try {
                // TODO: 음성 파일도 함께 API에 전달
                val analysis = photoRepository.analyzeImage(imageBytes)

                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false,
                        visionAnalysis = analysis
                    )
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(isProcessingImage = false)
                }
                _uiEffect.emit(CameraUiEffect.ShowError("이미지를 처리하는 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    /**
     * 음성 없이 이미지만 처리 (AI API 호출)
     */
    private fun processImageWithoutVoice(imageBytes: ByteArray) {
        processImageWithVoice(imageBytes, null)
    }

    /**
     * 카메라 상태를 대기 상태로 초기화
     */
    fun resetCameraState() {
        _uiState.update { currentState ->
            currentState.copy(
                cameraState = CameraState.Idle,
                isCapturing = false,
                shouldCapture = false,
                capturedImageUri = null,
                selectedImageUri = null,
                showGalleryPicker = false,
                showCapturedImageDialog = false,
                showVoiceRecordingDialog = false,
                isProcessingImage = false,
                visionAnalysis = null
            )
        }
    }
}
