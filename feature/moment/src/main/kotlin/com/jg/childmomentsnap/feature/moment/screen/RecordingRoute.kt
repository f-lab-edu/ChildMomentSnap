package com.jg.childmomentsnap.feature.moment.screen

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.jg.childmomentsnap.core.model.VisionAnalysis
import com.jg.childmomentsnap.core.ui.permissions.AppPermissions
import com.jg.childmomentsnap.core.ui.permissions.hasAllPermissions
import com.jg.childmomentsnap.feature.moment.R
import com.jg.childmomentsnap.feature.moment.RecordingState
import com.jg.childmomentsnap.feature.moment.model.VoiceRecordingError
import com.jg.childmomentsnap.feature.moment.model.VoiceRecordingUiEffect
import com.jg.childmomentsnap.feature.moment.util.rememberSpeechToTextManager
import com.jg.childmomentsnap.feature.moment.viewmodel.VoiceRecordingViewModel

private const val FILE_NAME = "recording.mp4"

/**
 * RecordingScreen의 Navigation Entry Point
 *
 * 이미지 URI와 AI 분석 결과를 Navigation 인자로 받아서 프로세스 종료 시에도 복원 가능합니다.
 */
@Composable
fun RecordingRoute(
    imageUri: String,
    visionAnalysisContent: String,
    visionAnalysis: VisionAnalysis,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: VoiceRecordingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val speechToTextManager = rememberSpeechToTextManager()
    val sttState by speechToTextManager.state.collectAsStateWithLifecycle()

    // 음성 권한 요청 Launcher
    val voicePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ -> }
    )

    // 권한 상태 확인
    val hasVoicePermission = context.hasAllPermissions(AppPermissions.Groups.getVoicePermissions())


    // 초기 설정
    LaunchedEffect(Unit) {
        // 녹음 파일 경로 설정
        val recordingFilePath = "${context.externalCacheDir?.absolutePath}/$FILE_NAME"
        viewModel.initialize(
            visionAnalysisContent = visionAnalysisContent,
            visionAnalysis = visionAnalysis,
            uri = imageUri,
            filePath = recordingFilePath
        )
    }

    LaunchedEffect(sttState.spokenText, sttState.error) {
        if (sttState.spokenText.isNotBlank()) {
            viewModel.appendSttResult(sttState.spokenText)

            if (uiState.recordingState == RecordingState.RECODING) {
                speechToTextManager.startListening()
            }
        }
        if (sttState.error?.isNotBlank() == true) {
            Toast.makeText(context, sttState.error, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(sttState.isSpeaking) {
        if (sttState.isSpeaking) {
            speechToTextManager.amplitude.collect { normalizedRms ->
                viewModel.updateSttAmplitude(normalizedRms)
            }
        }
    }

    // UI Effects 처리
    LaunchedEffect(Unit) {
        viewModel.uiEffect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is VoiceRecordingUiEffect.ShowErrorToast -> {
                        val message = getVoiceRecordingErrorMessage(context, effect.errorType)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }

                    is VoiceRecordingUiEffect.MomentAnalysisCompleted -> {
                        // AI 분석 완료 처리
                        onCompleted()
                    }

                    is VoiceRecordingUiEffect.NotifyRecordingCompleted -> {
                        onCompleted()
                    }
                }
            }
    }

    RecordingScreen(
        capturedPhotoPath = imageUri,
        content = uiState.editedContent ?: "",
        onContentChange = viewModel::updateEditedContent,
        state = uiState.toRecordingControlsState(),
        amplitudes = uiState.amplitudes,
        // onReset = {  // 리셋 버튼 제거
        //     speechToTextManager.stopListening()
        //     viewModel.resetRecording()
        // },
        onRecordingStart = {
            speechToTextManager.startListening()
            viewModel.startSttListeningState()
        },
        onRecordingPause = {
            speechToTextManager.stopListening()
            viewModel.pauseSttListeningState()
        },
        onRecordingResume = {
            speechToTextManager.startListening()
            viewModel.resumeSttListeningState()
        },
        onRecordingStop = {
            speechToTextManager.stopListening()
            viewModel.stopSttListeningState()
        },
        onPlaybackStart = viewModel::playRecording,
        onPlaybackStop = viewModel::stopPlayback,
        onCompleted = viewModel::finishRecording,
        isProcessing = uiState.isProcessing,
        visionAnalysis = uiState.visionAnalysis,
        hasVoicePermission = hasVoicePermission,
        onRequestVoicePermission = {
            voicePermissionLauncher.launch(
                AppPermissions.Groups.getVoicePermissions().toTypedArray()
            )
        },
        onBackClick = onBackClick
    )
}

/**
 * VoiceRecordingError enum을 String Resource 기반 에러 메시지로 변환
 */
private fun getVoiceRecordingErrorMessage(
    context: Context,
    errorType: VoiceRecordingError
): String {
    return when (errorType) {
        VoiceRecordingError.RECORDING_FILE_PATH_NOT_SET ->
            context.getString(R.string.feature_moment_error_recording_file_path_not_set)

        VoiceRecordingError.RECORDING_START_FAILED ->
            context.getString(R.string.feature_moment_error_recording_start_failed)

        VoiceRecordingError.RECORDING_PAUSE_FAILED ->
            context.getString(R.string.feature_moment_error_recording_pause_failed)

        VoiceRecordingError.RECORDING_RESUME_FAILED ->
            context.getString(R.string.feature_moment_error_recording_resume_failed)

        VoiceRecordingError.RECORDING_STOP_FAILED ->
            context.getString(R.string.feature_moment_error_recording_stop_failed)

        VoiceRecordingError.NO_RECORDING_FILE_TO_PLAY ->
            context.getString(R.string.feature_moment_error_no_recording_file_to_play)

        VoiceRecordingError.RECORDING_FILE_NOT_FOUND ->
            context.getString(R.string.feature_moment_error_recording_file_not_found)

        VoiceRecordingError.PLAYBACK_START_FAILED ->
            context.getString(R.string.feature_moment_error_playback_start_failed)

        VoiceRecordingError.PLAYBACK_ERROR ->
            context.getString(R.string.feature_moment_error_playback_error)

        VoiceRecordingError.PLAYBACK_STOP_FAILED ->
            context.getString(R.string.feature_moment_error_playback_stop_failed)

        VoiceRecordingError.RESET_FAILED ->
            context.getString(R.string.feature_moment_error_reset_failed)

        VoiceRecordingError.IMAGE_FILE_PATH_NOT_SET ->
            context.getString(R.string.feature_moment_error_reset_failed)

        VoiceRecordingError.PHOTO_DIARY_GENERATION_FAILED ->
            context.getString(R.string.feature_moment_error_photo_diary)
    }
}
