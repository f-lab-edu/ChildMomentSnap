package com.jg.childmomentsnap.feature.moment.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jg.childmomentsnap.core.ui.permissions.AppPermissions
import com.jg.childmomentsnap.feature.moment.PermissionState
import com.jg.childmomentsnap.feature.moment.R


@Composable
internal fun ConfirmStartRecordingDialog(
    voicePermissionState: PermissionState,
    onVoicePermissionResult: (Map<String, Boolean>, Boolean) -> Unit,
    onConfirmVoiceRecording: () -> Unit,
    onSkipVoiceRecording: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // 음성 권한 요청 런처
    val voicePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 영구적으로 거부된 권한 확인
        val hasAnyPermanentlyDenied = permissions.any { (permission, granted) ->
            if (granted) {
                false // 허용된 권한은 영구 거부가 아님
            } else {
                // 거부된 권한 중에서 rationale을 보여주지 않는 경우가 영구 거부
                activity?.shouldShowRequestPermissionRationale(permission) == false
            }
        }

        // 모든 권한이 거부되고 rationale을 보여주지 않는 경우만 영구 거부로 판단
        val allDenied = permissions.values.all { !it }
        val shouldShowRationale = permissions.keys.any { permission ->
            activity?.shouldShowRequestPermissionRationale(permission) == true
        }

        val finalPermanentlyDenied = allDenied && !shouldShowRationale && hasAnyPermanentlyDenied

        onVoicePermissionResult(permissions, finalPermanentlyDenied)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 아이콘
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.feature_moment_voice_recording_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 설명
                Text(
                    text = stringResource(R.string.feature_moment_voice_recording_description),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 권한에 따른 버튼들
                when (voicePermissionState) {
                    PermissionState.Granted -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onSkipVoiceRecording,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.feature_moment_skip_recording),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            Button(
                                onClick = onConfirmVoiceRecording,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.feature_moment_start_recording),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                    
                    PermissionState.Denied -> {
                        // 권한이 거부된 경우: 권한 요청 / 건너뛰기
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onSkipVoiceRecording,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stringResource(R.string.feature_moment_skip_recording),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Button(
                                onClick = {
                                    voicePermissionLauncher.launch(AppPermissions.Groups.getVoicePermissions().toTypedArray())
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stringResource(R.string.feature_moment_grant_voice_permission),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    PermissionState.PermanentlyDenied -> {
                        // 권한이 영구적으로 거부된 경우: 설정으로 이동 / 건너뛰기
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.feature_moment_voice_permission_permanently_denied),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onSkipVoiceRecording,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.feature_moment_skip_recording))
                                }
                                
                                Button(
                                    onClick = {
                                        // 앱 설정 화면으로 이동
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.feature_moment_go_to_settings))
                                }
                            }
                        }
                    }
                    
                    PermissionState.Checking -> {
                        // 권한 확인 중인 경우: 로딩 상태 표시
                        Button(
                            onClick = { },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.feature_moment_checking_voice_permission))
                        }
                    }
                }
            }
        }
    }
}