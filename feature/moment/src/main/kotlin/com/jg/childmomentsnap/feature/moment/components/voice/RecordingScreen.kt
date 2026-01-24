package com.jg.childmomentsnap.feature.moment.components.voice

import com.jg.childmomentsnap.feature.moment.components.common.MomentChip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.jg.childmomentsnap.feature.moment.RecordingControlsState
import kotlin.math.sin
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.SolidColor
import com.jg.childmomentsnap.core.model.VisionAnalysis
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.jg.childmomentsnap.feature.moment.R
/**
 * 1. 고도화된 음성 녹음 및 실시간 STT 화면
 * 기존 BottomSheet의 정밀한 제어 기능과 전체 화면의 감성적인 레이아웃을 결합했습니다.
 */
@Composable
fun RecordingScreen(
    capturedPhotoPath: String,
    sttText: String,
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
    onPlaying: () -> Unit = {}, // Not used but compliant with previous? No wait, strict sig.
    visionAnalysis: VisionAnalysis? = null,
    hasVoicePermission: Boolean = false,
    onRequestVoicePermission: () -> Unit = {},
    onCompleted: () -> Unit = {}
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
    MomentsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // 상단 콘텐츠 영역
                Column(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 상단: 촬영된 사진 작은 프리뷰
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
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // 중앙: 분석 결과 칩 & 텍스트 편집 영역
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Moment Chips
                       if (visionAnalysis?.labels?.isNotEmpty() == true) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.Center,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp)
                            ) {
                                items(visionAnalysis.labels) { label ->
                                    MomentChip(
                                        text = "#${label.description}",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                            }
                        }

                        // 텍스트 편집 카드
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 180.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = Stone50)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // STT 텍스트가 들어오면 그것을 보여주고, 아니면 분석된 텍스트나 힌트 표시
                                val displayText = sttText.ifEmpty { visionAnalysis?.detectedText ?: "" }
                                
                                Text(
                                    text = if (displayText.isEmpty()) stringResource(R.string.feature_moment_recording_screen_stt_placeholder) else displayText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = if (displayText.isEmpty()) Stone300 else Stone800,
                                    lineHeight = 28.sp
                                )
                            }
                        }
                    }
                }

                // 하단 컨트롤 영역
                Column(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 하단: 음성 진폭 시각화 (Canvas 기반)
                    VoiceAmplitudeVisualizer(
                        amplitudes = amplitudes,
                        isRecording = state.isRecording,
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 녹음 제어 버튼 영역 (권한 있을 때만 표시)
                    if (hasVoicePermission) {
                        RecordingControls(
                            state = state,
                            onReset = onReset,
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // 최종 완료 버튼 (녹음 완료 또는 텍스트 모드일 때 표시)
                    // 텍스트 모드일 때는 항상 표시, 녹음 모드일 때는 정지/재생 상태일 때 표시
                    val showCompleteButton = if (hasVoicePermission) {
                        state.isStopped || state.isPlaying
                    } else {
                        // 텍스트 모드면 항상 표시 (단, 처리중 아닐때)
                        true
                    }

                    AnimatedVisibility(
                        visible = showCompleteButton,
                         modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Button(
                            onClick = onCompleted,
                            modifier = Modifier.fillMaxSize(),
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
                                Text(stringResource(R.string.feature_moment_recording_screen_button_completed), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
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
                barHeight * (0.8f + 0.2f * sin(index * 0.5f + System.currentTimeMillis() * 0.01f).coerceAtLeast(0f))
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
    onReset: () -> Unit,
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
        // 리셋 버튼
        IconButton(
            onClick = onReset,
            enabled = state.canResetRecording,
            modifier = Modifier.size(48.dp).background(Stone100, CircleShape)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.feature_moment_recording_screen_action_reset), tint = if (state.canResetRecording) Stone800 else Stone200)
        }

        // 메인 녹음/일시정지 버튼
        FloatingActionButton(
            onClick = {
                when {
                    state.isPlaying -> onPlaybackStop()
                    state.canPlayRecording -> onPlaybackStart()
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
                state.canPlayRecording -> Icons.Default.PlayArrow
                state.isRecording -> Icons.Default.Pause
                else -> Icons.Default.Mic
            }
            Icon(icon, contentDescription = stringResource(R.string.feature_moment_recording_screen_action_main), modifier = Modifier.size(36.dp))
        }

        // 정지 버튼
        IconButton(
            onClick = {
                if (state.isPlaying) onPlaybackStop()
                else if (state.canStopRecording) onRecordingStop()
            },
            enabled = state.canStopRecording || state.isPlaying,
            modifier = Modifier.size(48.dp).background(Stone100, CircleShape)
        ) {
            Icon(Icons.Default.Stop, contentDescription = stringResource(R.string.feature_moment_recording_screen_action_stop), tint = if (state.canStopRecording || state.isPlaying) Stone800 else Stone200)
        }
    }
}
