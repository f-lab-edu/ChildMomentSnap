package com.jg.childmomentsnap.feature.moment.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jg.childmomentsnap.core.model.VisionAnalysis
import com.jg.childmomentsnap.core.model.VisionLikelihood
import com.jg.childmomentsnap.core.ui.theme.Amber500
import com.jg.childmomentsnap.core.ui.theme.MomentsTheme
import com.jg.childmomentsnap.core.ui.theme.Rose400
import com.jg.childmomentsnap.core.ui.theme.Stone100
import com.jg.childmomentsnap.core.ui.theme.Stone200
import com.jg.childmomentsnap.core.ui.theme.Stone300
import com.jg.childmomentsnap.core.ui.theme.Stone400
import com.jg.childmomentsnap.core.ui.theme.Stone50
import com.jg.childmomentsnap.core.ui.theme.Stone800
import com.jg.childmomentsnap.core.ui.theme.Stone900
import com.jg.childmomentsnap.feature.moment.R
import com.jg.childmomentsnap.feature.moment.RecordingControlsState
import com.jg.childmomentsnap.feature.moment.components.common.MomentChip
import kotlin.math.sin

/**
 * 1. 고도화된 음성 녹음 및 실시간 STT 화면
 */
@Composable
fun RecordingScreen(
    capturedPhotoPath: String,
    content: String,
    onContentChange: (String) -> Unit,
    state: RecordingControlsState,
    amplitudes: List<Float> = emptyList(),
    isProcessing: Boolean = false,
    onReset: () -> Unit = {},
    onRecordingStart: () -> Unit = {},
    onRecordingPause: () -> Unit = {},
    onRecordingResume: () -> Unit = {},
    onRecordingStop: () -> Unit = {},
    onPlaybackStart: () -> Unit = {},
    onPlaybackStop: () -> Unit = {},
    onPlaying: () -> Unit = {},
    emotionChips: List<Int> = emptyList(),
    hasVoicePermission: Boolean = false,
    onRequestVoicePermission: () -> Unit = {},
    onCompleted: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    // 권한 요청 다이얼로그 상태
    var showPermissionRationale by remember { mutableStateOf(false) }

    // 텍스트 필드 포커스 제어
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 초기 진입 시 권한 확인
    LaunchedEffect(Unit) {
        if (!hasVoicePermission) {
            showPermissionRationale = true
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = {
                showPermissionRationale = false
                // 거절 시 텍스트 포커스
                focusRequester.requestFocus()
                keyboardController?.show()
            },
            title = { Text(text = stringResource(R.string.feature_moment_recording_permission_title)) },
            text = { Text(text = stringResource(R.string.feature_moment_recording_permission_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionRationale = false
                        onRequestVoicePermission()
                    }
                ) {
                    Text(stringResource(R.string.feature_moment_recording_permission_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionRationale = false
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                ) {
                    Text(stringResource(R.string.feature_moment_recording_permission_dismiss))
                }
            },
            containerColor = Color.White
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopTitle(
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 상단: 촬영된 사진 작은 프리뷰
                CaptureThumbnail(
                    capturedPhotoPath = capturedPhotoPath
                )

                StateGuideMessage(state = state)

                // Moment Chips
                if (emotionChips.isNotEmpty()) {
                    FaceMomentChip(emotionChips)
                }

                // 텍스트 편집 카드
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()) {
                    TextEditorCard(
                        content = content,
                        onContentChange = onContentChange
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 하단: 음성 진폭 시각화 (Canvas 기반)
                if (amplitudes.isNotEmpty()) {
                    VoiceAmplitudeVisualizer(
                        amplitudes = amplitudes,
                        isRecording = state.isRecording,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }


                // 녹음 제어 버튼 영역 (권한 있을 때만 표시)
                if (hasVoicePermission) {
                    RecordingControls(
                        state = state,
                        // onReset = onReset,  // 리셋 버튼 제거
                        onRecordingStart = onRecordingStart,
                        onRecordingPause = onRecordingPause,
                        onRecordingResume = onRecordingResume,
                        onRecordingStop = onRecordingStop,
                        onPlaybackStart = onPlaybackStart,
                        onPlaybackStop = onPlaybackStop
                    )
                } else {
                    // 권한 없을 때는 빈 공간 확보 (또는 텍스트 입력 안내)
                    Spacer(modifier = Modifier.height(80.dp))
                }

                Button(
                    onClick = onCompleted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isProcessing,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            stringResource(R.string.feature_moment_recording_screen_button_completed),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun TopTitle(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.clickable(onClick = onBackClick),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.feature_moment_back_button),
            tint = Color.Black
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.feature_moment_recording_screen_title_recording),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

@Composable
private fun CaptureThumbnail(
    capturedPhotoPath: String
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Stone100)
    ) {
        if (capturedPhotoPath.isNotEmpty()) {
            AsyncImage(
                model = capturedPhotoPath,
                contentDescription = stringResource(R.string.feature_moment_recording_screen_photo_description),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                tint = Stone200
            )
        }
    }
}

@Composable
fun FaceMomentChip(
    emotionChips: List<Int>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        items(emotionChips) { resId ->
            MomentChip(
                text = stringResource(resId),
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
private fun StateGuideMessage(
    state: RecordingControlsState
) {
    Text(
        text = when {
            state.isRecording -> stringResource(R.string.feature_moment_recording_screen_title_recording)
            state.isPlaying -> stringResource(R.string.feature_moment_recording_screen_title_playing)
            state.isStopped -> stringResource(R.string.feature_moment_recording_screen_title_stopped)
            else -> stringResource(R.string.feature_moment_recording_screen_title_default)
        },
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Stone900
    )

    Text(
        text = stringResource(R.string.feature_moment_recording_screen_subtitle),
        style = MaterialTheme.typography.bodySmall,
        color = Stone400,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun TextEditorCard(
    content: String,
    onContentChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Stone50)
    ) {
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            contentAlignment = Alignment.TopStart
        ) {
            if (content.isEmpty()) {
                Text(
                    text = stringResource(R.string.feature_moment_recording_screen_stt_placeholder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Stone300,
                    textAlign = TextAlign.Start
                )
            }

            BasicTextField(
                value = content,
                onValueChange = onContentChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Stone800,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxSize(),
                cursorBrush = SolidColor(Amber500)
            )
        }
    }
}

/**
 * Canvas를 활용한 실시간 음성 파형 시각화
 */
@Composable
private fun VoiceAmplitudeVisualizer(
    amplitudes: List<Float>,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        // 데이터가 없을 때 기본 파형 생성
        val data = amplitudes.ifEmpty { List(30) { 0.1f } }
        val barWidth = width / data.size

        data.forEachIndexed { index, amplitude ->
            val x = index * barWidth + barWidth / 2
            val barHeight = (amplitude * height * 0.9f).coerceAtLeast(4.dp.toPx())

            // 녹음 중일 때 약간의 무작위 애니메이션 효과 가미
            val animatedHeight = if (isRecording) {
                barHeight * (0.8f + 0.2f * sin(index * 0.5f + System.currentTimeMillis() * 0.01f).coerceAtLeast(
                    0f
                ))
            } else barHeight

            drawLine(
                color = if (isRecording) Rose400 else Stone200,
                start = Offset(x, centerY - animatedHeight / 2),
                end = Offset(x, centerY + animatedHeight / 2),
                strokeWidth = (barWidth * 0.6f).coerceAtMost(6.dp.toPx())
            )
        }
    }
}

/**
 * 고도화된 녹음 제어 버튼 그룹
 */
@Composable
private fun RecordingControls(
    state: RecordingControlsState,
    // onReset: () -> Unit,  // 리셋 버튼 제거 (추후 녹음 기능 동작 여부로 인해 임시 주석)
    onRecordingStart: () -> Unit,
    onRecordingPause: () -> Unit,
    onRecordingResume: () -> Unit,
    onRecordingStop: () -> Unit,
    onPlaybackStart: () -> Unit,
    onPlaybackStop: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 리셋 버튼 주석 처리 (공간 설정)
        IconButton(
            onClick = {  },
            enabled = state.canResetRecording,
            modifier = Modifier
                .size(48.dp)
//                .background(Stone100, CircleShape)
        ) {
//            Icon(
//                Icons.Default.Refresh,
//                contentDescription = stringResource(R.string.feature_moment_recording_screen_action_reset),
//                tint = if (state.canResetRecording) Stone800 else Stone200
//            )
        }

        // 메인 녹음/일시정지 버튼
        //  (26.02.11 녹음기능은 임시 중단 되고 STT 기능만 사용하기에 플레이 기능은 임시 주석)
        FloatingActionButton(
            onClick = {
                when {
                    state.isPlaying -> onPlaybackStop()
//                    state.canPlayRecording -> onPlaybackStart()
                    state.canStartRecording -> onRecordingStart()
                    state.canPauseRecording -> onRecordingPause()
                    state.canResumeRecording -> onRecordingResume()
                }
            },
            containerColor = if (state.isRecording) Rose400 else Stone900,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            val icon = when {
                state.isPlaying -> Icons.Default.Pause
//                state.canPlayRecording -> Icons.Default.PlayArrow
                state.isRecording -> Icons.Default.Pause
                else -> Icons.Default.Mic
            }
            Icon(
                icon,
                contentDescription = stringResource(R.string.feature_moment_recording_screen_action_main),
                modifier = Modifier.size(36.dp)
            )
        }

        // 정지 버튼
        IconButton(
            onClick = {
                if (state.isPlaying) onPlaybackStop()
                else if (state.canStopRecording) onRecordingStop()
            },
            enabled = state.canStopRecording || state.isPlaying,
            modifier = Modifier
                .size(48.dp)
                .background(Stone100, CircleShape)
        ) {
            Icon(
                Icons.Default.Stop,
                contentDescription = stringResource(R.string.feature_moment_recording_screen_action_stop),
                tint = if (state.canStopRecording || state.isPlaying) Stone800 else Stone200
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordingScreenPreview() {
    MomentsTheme {
        RecordingScreen(
            capturedPhotoPath = "",
            content = "우리 아가 최고",
            onContentChange = {},
            state = RecordingControlsState(
                isRecording = false,
                isStopped = false,
                isPlaying = false,
                isPaused = false,
                canStartRecording = true,
                canPauseRecording = false,
                canResumeRecording = false,
                canStopRecording = false,
                canResetRecording = false,
                canPlayRecording = false,
            ),
            amplitudes = emptyList(),
            emotionChips = listOf(
                R.string.feature_moment_emotion_joy,
                R.string.feature_moment_emotion_surprise
            )
        )
    }
}