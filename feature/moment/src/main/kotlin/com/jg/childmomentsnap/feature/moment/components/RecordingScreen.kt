package com.jg.childmomentsnap.feature.moment.components

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
    onCompleted: () -> Unit = {}
) {
    println("!! RecordingScreen")
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
                                contentDescription = "촬영된 사진",
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
                            state.isRecording -> "아이의 순간을 기록하고 있어요"
                            state.isPlaying -> "남겨주신 목소리를 듣고 있어요"
                            state.isStopped -> "멋진 이야기가 완성되었네요!"
                            else -> "이 순간에 대해 들려주세요"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Stone900
                    )
                    
                    Text(
                        text = "아이의 표정이나 당시의 기분도 좋아요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone400,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 중앙: 실시간 STT 텍스트 영역
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
                            Text(
                                text = if (sttText.isEmpty()) "목소리를 기다리고 있어요..." else sttText,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = if (sttText.isEmpty()) Stone300 else Stone800,
                                lineHeight = 28.sp
                            )
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

                    // 녹음 제어 버튼 영역 (3버튼 구조)
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // 최종 완료 버튼 (분석 단계 진입)
                    AnimatedVisibility(
                        visible = state.isStopped || state.isPlaying,
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
                                Text("이대로 AI 일기 만들기", fontWeight = FontWeight.Bold)
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
        val data = if (amplitudes.isEmpty()) List(30) { 0.1f } else amplitudes
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
            Icon(Icons.Default.Refresh, contentDescription = "리셋", tint = if (state.canResetRecording) Stone800 else Stone200)
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
            Icon(icon, contentDescription = "메인 액션", modifier = Modifier.size(36.dp))
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
            Icon(Icons.Default.Stop, contentDescription = "정지", tint = if (state.canStopRecording || state.isPlaying) Stone800 else Stone200)
        }
    }
}
