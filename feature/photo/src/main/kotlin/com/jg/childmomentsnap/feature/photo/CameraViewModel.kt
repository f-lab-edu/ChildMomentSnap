package com.jg.childmomentsnap.feature.photo

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.data.repository.PhotoRepository
import com.jg.childmomentsnap.feature.photo.model.CameraUiEffect
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
     * 현재 권한 상태를 기반으로 권한 상태를 업데이트
     */
    fun updatePermissionState(hasAllPermissions: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                permissionState = if (hasAllPermissions) {
                    PermissionState.Granted
                } else {
                    PermissionState.Denied
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
                permissionState = when {
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
    fun confirmSelectedImage(app: Context) {
        val currentUri = _uiState.value.selectedImageUri ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true, visionAnalysis = null) }
            try {
                val imageBytes = app.contentResolver.openInputStream(currentUri)?.use { it.readBytes() }
                    ?: throw IllegalArgumentException("Cannot open input stream for URI: $currentUri")

                val analysis = photoRepository.analyzeImage(imageBytes)

                // 성공 시, 결과 화면으로 이동하거나 상태를 업데이트 합니다.
                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false,
                        selectedImageUri = null, // 성공 후 선택된 이미지 초기화
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
     * 촬영된 사진 사용을 확인하고 AI 처리를 시작
     */
    fun confirmCapturedImage(app: Context) {
        val currentUri = _uiState.value.capturedImageUri ?: return
        
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProcessingImage = true,
                    showCapturedImageDialog = false,
                    visionAnalysis = null
                )
            }
            try {
                val imageBytes = app.contentResolver.openInputStream(currentUri)?.use { it.readBytes() }
                    ?: throw IllegalArgumentException("Cannot open input stream for URI: $currentUri")

                val analysis = photoRepository.analyzeImage(imageBytes)

                // 성공 시, 결과 화면으로 이동하거나 상태를 업데이트 합니다.
                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false,
                        capturedImageUri = null, // 성공 후 캡쳐된 이미지 초기화
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
     * 촬영된 사진을 취소하고 다시 촬영
     */
    fun retakeCapturedPhoto() {
        _uiState.update { currentState ->
            currentState.copy(
                capturedImageUri = null,
                showCapturedImageDialog = false
            )
        }
    }
    
    /**
     * TODO 촬영된 사진 확인 다이얼로그를 닫기
     */
    fun dismissCapturedImageDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                showCapturedImageDialog = false,
                capturedImageUri = null
            )
        }
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
                isProcessingImage = false,
                visionAnalysis = null
            )
        }
    }
}
