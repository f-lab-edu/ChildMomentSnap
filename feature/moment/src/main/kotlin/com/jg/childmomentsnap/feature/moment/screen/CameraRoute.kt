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
import com.jg.childmomentsnap.feature.moment.components.VoiceRecordingBottomSheet
import com.jg.childmomentsnap.feature.moment.model.CameraUiEffect
import com.jg.childmomentsnap.feature.moment.PermissionState
import com.jg.childmomentsnap.feature.moment.model.VoiceRecordingNavigationEvent
import com.jg.childmomentsnap.feature.moment.model.VoiceRecordingUiEffect
import com.jg.childmomentsnap.feature.moment.model.VoiceRecordingError
import com.jg.childmomentsnap.feature.moment.viewmodel.VoiceRecordingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context

/**
 * ViewModel과 통합된 카메라 라우트 진입점
 *
 * 이 컴포저블은 카메라 기능의 메인 진입점 역할을 하며,
 * ViewModel 통합과 UI 레이어에서의 권한 확인을 처리합니다.
 *
 * @param onNavigateUp 뒤로 이동 콜백
 * @param modifier 스타일링을 위한 Modifier
 * @param viewModel 카메라 ViewModel 인스턴스
 */
@Composable
internal fun CameraRoute(
    onBackClick: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    voiceViewModel: VoiceRecordingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val voiceUiState by voiceViewModel.uiState.collectAsStateWithLifecycle()
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

    // 앱이 포그라운드로 돌아올 때 권한 재확인
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity
    var lastPermissionState by remember { mutableStateOf<Boolean?>(null) }
    val imageBytes: ByteArray? by produceState<ByteArray?>(
        initialValue = null,
        key1 = uiState.capturedImageUri
    ) {
        val uri = uiState.capturedImageUri
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
                }
            }
    }

    // VoiceRecording UI Effects 처리
    LaunchedEffect(Unit) {
        voiceViewModel.uiEffect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is VoiceRecordingUiEffect.ShowErrorToast -> {
                        val message = getVoiceRecordingErrorMessage(context, effect.errorType)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                    is VoiceRecordingUiEffect.MomentAnalysisCompleted -> {
                        // AI 분석 완료 처리
                        // TODO: 결과 데이터를 가지고 다음 화면(글쓰기/확인)으로 이동
                        // 우선은 이전 로직 유지하여 종료 처리
                         imageBytes?.let { bytes ->
                             viewModel.onVoiceRecordingCompleted(bytes, effect.momentData.transcription)
                             onNavigateUp()
                         }
                    }
                    is VoiceRecordingUiEffect.NotifyRecordingCompleted -> {
                        // 이전 호환성을 위해 유지 (필요 없는 경우 제거 가능)
                        onNavigateUp()
                    }
                }
            }
    }

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
            imageBytes?.let {
                viewModel.confirmSelectedImage(
                    imageBytes = it
                )
            }
        },
        onCancelImage = viewModel::cancelSelectedImage,
        onClearError = viewModel::clearError,
        onCameraError = viewModel::onCameraError,
        onPhotoCaptured = viewModel::onPhotoCaptured,
        onCaptureComplete = viewModel::onCaptureComplete,
        onConfirmCapturedImage = {
            viewModel.confirmCapturedImage()
        },
        onRetakeCapturedPhoto = viewModel::retakeCapturedPhoto,
        onConfirmVoiceRecording = {
            imageBytes?.let {
                viewModel.confirmVoiceRecording(it)
                // 파일 경로 설정
                val recordingFilePath = "${context.externalCacheDir?.absolutePath}/$FILE_NAME"
                voiceViewModel.setVoiceRecordingFilePath(recordingFilePath)
                // BottomSheet 표시
                if (uiState.voicePermissionState == PermissionState.Granted) {
                    voiceViewModel.showVoiceRecordingBottomSheet()
                }
            }
        },
        onSkipVoiceRecording = {
            imageBytes?.let { 
                viewModel.skipVoiceRecording(it)
                onNavigateUp()
            }
        },
        onDismissVoiceRecordingDialog = viewModel::dismissVoiceRecordingDialog,
        onNavigateUp = onNavigateUp,
        modifier = modifier
    )

    if (voiceUiState.showVoiceRecordingBottomSheet) {
        VoiceRecordingBottomSheet(
            state = voiceUiState.toRecordingControlsState(),
            amplitudes = voiceUiState.amplitudes,
            onReset = voiceViewModel::resetRecording,
            onRecordingStart = voiceViewModel::startRecording,
            onRecordingPause = voiceViewModel::pauseRecording,
            onRecordingResume = voiceViewModel::resumeRecording,
            onRecordingStop = voiceViewModel::stopRecording,
            onPlaybackStart = voiceViewModel::playRecording,
            onPlaybackStop = voiceViewModel::stopPlayback,
            onCompleted = voiceViewModel::finishRecording,
            isProcessing = voiceUiState.isProcessing,
            onDismiss = voiceViewModel::onBottomSheetDismissed
        )
    }
}

private const val FILE_NAME = "recording.mp4"

/**
 * VoiceRecordingError enum을 String Resource 기반 에러 메시지로 변환
 */
private fun getVoiceRecordingErrorMessage(context: Context, errorType: VoiceRecordingError): String {
    return when (errorType) {
        VoiceRecordingError.RECORDING_FILE_PATH_NOT_SET -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_recording_file_path_not_set)
        VoiceRecordingError.RECORDING_START_FAILED -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_recording_start_failed)
        VoiceRecordingError.RECORDING_PAUSE_FAILED -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_recording_pause_failed)
        VoiceRecordingError.RECORDING_RESUME_FAILED -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_recording_resume_failed)
        VoiceRecordingError.RECORDING_STOP_FAILED -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_recording_stop_failed)
        VoiceRecordingError.NO_RECORDING_FILE_TO_PLAY -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_no_recording_file_to_play)
        VoiceRecordingError.RECORDING_FILE_NOT_FOUND -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_recording_file_not_found)
        VoiceRecordingError.PLAYBACK_START_FAILED -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_playback_start_failed)
        VoiceRecordingError.PLAYBACK_ERROR -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_playback_error)
        VoiceRecordingError.PLAYBACK_STOP_FAILED -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_playback_stop_failed)
        VoiceRecordingError.RESET_FAILED -> 
            context.getString(com.jg.childmomentsnap.feature.moment.R.string.feature_moment_error_reset_failed)
    }
}
