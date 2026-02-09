package com.jg.childmomentsnap.feature.moment.screen

import android.app.Activity
import android.widget.Toast
import com.jg.childmomentsnap.feature.moment.viewmodel.CameraViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.jg.childmomentsnap.core.ui.permissions.hasAllPermissions
import com.jg.childmomentsnap.core.ui.permissions.AppPermissions
import com.jg.childmomentsnap.feature.moment.model.CameraUiEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.jg.childmomentsnap.core.model.VisionAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * ViewModel과 통합된 카메라 라우트 진입점
 *
 * 이 컴포저블은 카메라 기능의 메인 진입점 역할을 하며,
 * ViewModel 통합과 UI 레이어에서의 권한 확인을 처리합니다.
 *
 * @param onBackClick 뒤로 이동 콜백
 * @param onNavigateUp 뒤로 이동 콜백
 * @param onNavigateToRecording 이미지 확정 후 RecordingScreen으로 이동하는 콜백 (imageUri, visionAnalysisContent)
 * @param modifier 스타일링을 위한 Modifier
 * @param viewModel 카메라 ViewModel 인스턴스
 */
@Composable
internal fun CameraRoute(
    onBackClick: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToRecording: (imageUri: String, visionAnalysisContent: String, visionAnalysis: VisionAnalysis) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 뒤로가기 임시
    BackHandler(onBack = onBackClick)

    // 화면 진입 시 권한 확인
    LaunchedEffect(Unit) {
        val hasAllCameraPermissions =
            context.hasAllPermissions(AppPermissions.Groups.getPhotoPermissions())
        val hasAllVoicePermissions =
            context.hasAllPermissions(AppPermissions.Groups.getVoicePermissions())
        viewModel.updatePermissionState(hasAllCameraPermissions, hasAllVoicePermissions)
    }

    // 이미지 바이트 로드
    val imageBytes: ByteArray? by produceState<ByteArray?>(
        initialValue = null,
        key1 = uiState.capturedImageUri,
        key2 = uiState.selectedImageUri
    ) {
        val uri = uiState.selectedImageUri ?: uiState.capturedImageUri
        value = if (uri != null) {
            withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes()
                }
            }
        } else {
            null
        }
    }

    // 앱이 포그라운드로 돌아올 때 권한 재확인
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity

    var lastPermissionState by remember { mutableStateOf<Boolean?>(null) }

    DisposableEffect(lifecycleOwner, activity) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    val hasAllCameraPermissions =
                        context.hasAllPermissions(AppPermissions.Groups.getPhotoPermissions())
                    val hasAllVoicePermissions =
                        context.hasAllPermissions(AppPermissions.Groups.getVoicePermissions())
                    //  상태 변경 시에만 UI 업데이트
                    if (lastPermissionState != hasAllCameraPermissions) {
                        lastPermissionState = hasAllCameraPermissions
                        viewModel.updatePermissionState(hasAllCameraPermissions, hasAllVoicePermissions)
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    //  설정 변경 (화면 회전 등)이 아닐 때만 초기화
                    if (activity?.isChangingConfigurations == false) {
                        viewModel.resetCameraState()
                    }
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Camera UI Effects 처리
    LaunchedEffect(Unit) {
        viewModel.uiEffect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is CameraUiEffect.ShowError -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                    is CameraUiEffect.NavigateToRecording -> {
                        onNavigateToRecording(effect.imageUri, effect.visionAnalysisContent, effect.visionAnalysis)
                    }
                }
            }
    }

    Box(modifier = modifier.fillMaxSize()) {
        CameraScreen(
            uiState = uiState,
            onPermissionResult = viewModel::onPermissionResult,
            onVoicePermissionResult = { permissions, isPermanentlyDenied ->
                viewModel.onVoicePermissionResult(permissions, isPermanentlyDenied)
                // 권한 상태 업데이트
                val hasAllCameraPermissions =
                    context.hasAllPermissions(AppPermissions.Groups.getPhotoPermissions())
                val hasAllVoicePermissions =
                    context.hasAllPermissions(AppPermissions.Groups.getVoicePermissions())
                viewModel.updatePermissionState(hasAllCameraPermissions, hasAllVoicePermissions)
            },
            onCameraReady = viewModel::onCameraReady,
            onSwitchCamera = viewModel::switchCamera,
            onCapturePhoto = viewModel::capturePhoto,
            onGalleryClick = viewModel::openGallery,
            onImageSelected = viewModel::onImageSelected,
            onDismissGalleryPicker = viewModel::dismissGalleryPicker,
            onConfirmImage = {
                // 갤러리에서 선택한 이미지 확정 -> AI 분석 시작
                val imageUri = uiState.selectedImageUri?.toString()
                if (imageUri != null && imageBytes != null) {
                    viewModel.generatePhotoDiary(imageBytes!!, imageUri)
                }
            },
            onCancelImage = viewModel::cancelSelectedImage,
            onClearError = viewModel::clearError,
            onCameraError = viewModel::onCameraError,
            onPhotoCaptured = viewModel::onPhotoCaptured,
            onCaptureComplete = viewModel::onCaptureComplete,
            onConfirmCapturedImage = {
                // 촬영한 이미지 확정 -> AI 분석 시작
                val imageUri = uiState.capturedImageUri?.toString()
                if (imageUri != null && imageBytes != null) {
                    viewModel.generatePhotoDiary(imageBytes!!, imageUri)
                }
            },
            onRetakeCapturedPhoto = viewModel::retakeCapturedPhoto,
            onConfirmVoiceRecording = {
                // VoiceRecording 확인 -> AI 분석 시작
                val imageUri = (uiState.selectedImageUri ?: uiState.capturedImageUri)?.toString()
                if (imageUri != null && imageBytes != null) {
                    viewModel.generatePhotoDiary(imageBytes!!, imageUri)
                }
            },
            onSkipVoiceRecording = {
                // 음성 녹음 스킵 -> 바로 완료
                onNavigateUp()
            },
            onDismissVoiceRecordingDialog = viewModel::dismissVoiceRecordingDialog,
            onNavigateUp = onNavigateUp,
            modifier = Modifier.fillMaxSize()
        )
    }
}


