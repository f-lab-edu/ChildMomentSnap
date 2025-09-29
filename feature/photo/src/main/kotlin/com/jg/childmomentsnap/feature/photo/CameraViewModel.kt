package com.jg.childmomentsnap.feature.photo

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State Hoisting 패턴을 따르는 카메라 화면용 ViewModel
 * 
 * Android 의존성 없이 카메라 관련 모든 상태를 관리하여
 * 테스트하기 쉽고 클린 아키텍처 원칙을 따릅니다.
 */
@HiltViewModel
class CameraViewModel @Inject constructor(
    // TODO: Inject PhotoRepository when available
    // private val photoRepository: PhotoRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()
    
    /**
     * 현재 권한 상태를 기반으로 권한 상태를 업데이트합니다
     * 권한 확인 후 UI 레이어에서 호출됩니다
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
     * ActivityResult에서 권한 요청 결과를 처리합니다
     * 권한이 영구적으로 거부되었는지 판단합니다
     */
    fun onPermissionResult(
        permissions: Map<String, Boolean>,
        hasAnyPermanentlyDenied: Boolean = false
    ) {
        val allGranted = permissions.values.all { it }
        val hasAnyGranted = permissions.values.any { it }
        
        
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
     * 카메라 프리뷰가 성공적으로 초기화되었을 때 호출됩니다
     */
    fun onCameraReady() {
        _uiState.update { currentState ->
            currentState.copy(cameraState = CameraState.Previewing)
        }
    }
    
    /**
     * 전면과 후면 카메라를 전환합니다
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
     * 사진 촬영 프로세스를 시작합니다
     */
    fun capturePhoto() {
        _uiState.update { it.copy(isCapturing = true) }
        
        viewModelScope.launch {
            try {
                // TODO: PhotoRepository를 사용한 실제 사진 촬영 로직 구현
                // 현재는 촬영 프로세스를 시뮬레이션
                delay(1000)
                
                _uiState.update { currentState ->
                    currentState.copy(
                        isCapturing = false,
                        cameraState = CameraState.Previewing
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isCapturing = false,
                        errorMessage = "사진 촬영 중 오류가 발생했습니다: ${e.message}",
                        cameraState = CameraState.Error
                    )
                }
            }
        }
    }
    
    /**
     * 현재 에러 메시지를 지웁니다
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * 카메라 초기화 또는 사용 중 오류를 처리합니다
     */
    fun onCameraError(error: String) {
        _uiState.update { currentState ->
            currentState.copy(
                cameraState = CameraState.Error,
                errorMessage = error
            )
        }
    }
    
    /**
     * 갤러리를 열어 이미지를 선택합니다
     */
    fun openGallery() {
        _uiState.update { currentState ->
            currentState.copy(showGalleryPicker = true)
        }
    }
    
    /**
     * 갤러리에서 이미지를 선택한 후 처리합니다
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
     * 선택된 이미지 사용을 확인하고 AI 처리를 시작합니다
     */
    fun confirmSelectedImage() {
        val currentUri = _uiState.value.selectedImageUri ?: return
        
        viewModelScope.launch {
            try {
                _uiState.update { currentState ->
                    currentState.copy(isProcessingImage = true)
                }
                
                // TODO: 선택된 이미지를 처리하고 AI API 호출하여 글쓰기 화면으로 이동
                // 이미지 처리 시뮬레이션
                delay(2000)
                
                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false
                        // TODO: 글쓰기 화면으로 네비게이션 트리거
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isProcessingImage = false,
                        errorMessage = "이미지 처리 중 오류가 발생했습니다: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 선택된 이미지를 취소하고 카메라로 돌아갑니다
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
     * 갤러리 선택기를 닫습니다
     */
    fun dismissGalleryPicker() {
        _uiState.update { currentState ->
            currentState.copy(showGalleryPicker = false)
        }
    }
    
    /**
     * 카메라 상태를 대기 상태로 초기화합니다 (네비게이션에 유용)
     */
    fun resetCameraState() {
        _uiState.update { currentState ->
            currentState.copy(
                cameraState = CameraState.Idle,
                isCapturing = false,
                selectedImageUri = null,
                showGalleryPicker = false,
                isProcessingImage = false,
                errorMessage = null
            )
        }
    }
}