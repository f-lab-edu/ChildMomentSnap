package com.jg.childmomentsnap.feature.moment.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random
import com.jg.childmomentsnap.feature.moment.R
import com.jg.childmomentsnap.feature.moment.RecordingControlsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VoiceRecordingBottomSheet(
    state: RecordingControlsState,
    amplitudes: List<Float> = emptyList(),
    onReset: () -> Unit,
    onRecordingStart: () -> Unit,
    onRecordingPause: () -> Unit,
    onRecordingResume: () -> Unit,
    onRecordingStop: () -> Unit,
    onPlaybackStart: () -> Unit,
    onPlaybackStop: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상태 텍스트
            Text(
                text = when {
                    state.isRecording -> stringResource(R.string.feature_moment_bottom_sheet_recoding)
                    state.isPlaying -> stringResource(R.string.feature_moment_bottom_sheet_playing)
                    state.isPaused -> stringResource(R.string.feature_moment_bottom_sheet_paused)
                    state.isStopped -> stringResource(R.string.feature_moment_bottom_sheet_completed)
                    else -> stringResource(R.string.feature_moment_bottom_sheet_ready)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 음성 진폭 시각화
            VoiceAmplitudeVisualizer(
                amplitudes = amplitudes,
                isRecording = state.isRecording,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // 버튼 영역
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
        }
    }
}

@Composable
private fun VoiceAmplitudeVisualizer(
    amplitudes: List<Float>,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    // 데모용 진폭 데이터 (실제 로직이 없으므로)
    val demoAmplitudes = remember { 
        List(30) { Random.nextFloat() * 0.8f + 0.1f }
    }
    
    val displayAmplitudes = if (amplitudes.isNotEmpty()) amplitudes else demoAmplitudes
    
    Box(
        modifier = modifier
            .height(120.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRecording) {
            // 녹음 중일 때 - 실시간 파형
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawVoiceWaveform(
                    amplitudes = displayAmplitudes,
                    color = Color.Gray,
                    isAnimated = true
                )
            }
        } else {
            // 녹음 준비 상태 - 정적 파형 또는 플레이스홀더
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawVoiceWaveform(
                    amplitudes = displayAmplitudes.map { it * 0.3f }, // 낮은 진폭
                    color = Color.LightGray.copy(alpha = 0.5f),
                    isAnimated = false
                )
            }
        }
        
        // 중앙 마이크 아이콘
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isRecording) 
                        MaterialTheme.colorScheme.primary
                    else 
                        MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "마이크",
                modifier = Modifier.size(24.dp),
                tint = if (isRecording) 
                    MaterialTheme.colorScheme.onPrimary
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun DrawScope.drawVoiceWaveform(
    amplitudes: List<Float>,
    color: Color,
    isAnimated: Boolean
) {
    val width = size.width
    val height = size.height
    val centerY = height / 2
    val barWidth = width / amplitudes.size
    
    amplitudes.forEachIndexed { index, amplitude ->
        val x = index * barWidth + barWidth / 2
        val barHeight = (amplitude * height * 0.8f).coerceAtLeast(4.dp.toPx())
        
        // 애니메이션 효과를 위한 높이 조정
        val actualHeight = if (isAnimated) {
            barHeight * (0.7f + 0.3f * sin(index * 0.5f + System.currentTimeMillis() * 0.01f).coerceAtLeast(0f))
        } else {
            barHeight
        }
        
        drawLine(
            color = color,
            start = Offset(x, centerY - actualHeight / 2),
            end = Offset(x, centerY + actualHeight / 2),
            strokeWidth = (barWidth * 0.6f).coerceAtMost(8.dp.toPx())
        )
    }
}

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
        horizontalArrangement = Arrangement.spacedBy(54.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            onClick = { if (state.canResetRecording) onReset() },
            modifier = Modifier.size(54.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_replay),
                contentDescription = "리셋",
                tint = if (state.canResetRecording) Color.Unspecified else Color.Gray
            )
        }

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
            modifier = Modifier.size(54.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            val iconPainter = when {
                state.isPlaying -> R.drawable.ic_pause
                state.canPlayRecording -> R.drawable.ic_play_arrow
                state.isRecording -> R.drawable.ic_pause
                else -> R.drawable.ic_record
            }
            val contentDescription = when {
                state.isPlaying -> "재생 일시정지"
                state.canPlayRecording -> "재생"
                state.isRecording -> "일시정지"
                state.canResumeRecording -> "재개"
                else -> "녹음"
            }
            Icon(
                painter = painterResource(id = iconPainter),
                contentDescription = contentDescription
            )
        }

        FloatingActionButton(
            onClick = {
                if (state.isPlaying) {
                    onPlaybackStop()
                } else if (state.canStopRecording) {
                    onRecordingStop()
                }
            },
            modifier = Modifier.size(54.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_stop),
                contentDescription = "정지"
            )
        }
    }
}

@Composable
@Preview
private fun VoiceRecordingBottomSheetPreview() {
    VoiceRecordingBottomSheet(
        state = RecordingControlsState(
            canStartRecording = true,
            canPauseRecording = true,
            canResumeRecording = false,
            canStopRecording = true,
            canPlayRecording = false,
            canResetRecording = true,
            isRecording = false,
            isPlaying = false,
            isPaused = false,
            isStopped = false
        ),
        onReset = {},
        onRecordingStart = {},
        onRecordingPause = {},
        onRecordingResume = {},
        onRecordingStop = {},
        onPlaybackStart = {},
        onPlaybackStop = {},
        onDismiss = {}
    )
}
