package components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jg.childmomentsnap.core.ui.permissions.AppPermissions
import com.jg.childmomentsnap.feature.moment.CameraUiState
import com.jg.childmomentsnap.feature.moment.R
import com.jg.childmomentsnap.feature.moment.components.CameraBottomControls
import com.jg.childmomentsnap.feature.moment.components.CameraPreview
import com.jg.childmomentsnap.feature.moment.components.CameraTopBar

/**
 * 권한 확인 중에 표시되는 로딩 화면
 */
@Composable
internal fun CameraLoadingScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.feature_moment_checking_camera_permission),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // 뒤로가기 버튼
        IconButton(
            onClick = onNavigateUp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로 가기",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * 권한이 거부되었을 때 표시되는 권한 요청 화면
 */
@Composable
internal fun CameraPermissionScreen(
    onPermissionResult: (Map<String, Boolean>, Boolean) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    
    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 영구적으로 거부된 권한 확인 - 더 정확한 로직
        val hasAnyPermanentlyDenied = permissions.any { (permission, granted) ->
            if (granted) {
                false // 허용된 권한은 영구 거부가 아님
            } else {
                // 거부된 권한 중에서 rationale을 보여주지 않는 경우가 영구 거부
                // 단, 처음 요청하는 경우도 false를 반환하므로 구분 필요
                activity?.shouldShowRequestPermissionRationale(permission) == false
            }
        }
        
        // 모든 권한이 거부되고 rationale을 보여주지 않는 경우만 영구 거부로 판단
        val allDenied = permissions.values.all { !it }
        val shouldShowRationale = permissions.keys.any { permission ->
            activity?.shouldShowRequestPermissionRationale(permission) == true
        }
        
        val finalPermanentlyDenied = allDenied && !shouldShowRationale && hasAnyPermanentlyDenied
        
        
        onPermissionResult(permissions, finalPermanentlyDenied)
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.feature_moment_camera_permission_required),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.feature_moment_camera_permission_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {

                permissionLauncher.launch(AppPermissions.Groups.getPhotoPermissions().toTypedArray())
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.feature_moment_grant_permission))
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(onClick = onNavigateUp) {
            Text(stringResource(R.string.feature_moment_back_button))
        }
    }
}

/**
 * 권한이 영구적으로 거부되었을 때 표시되는 화면
 */
@Composable
internal fun CameraPermissionPermanentlyDeniedScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Block,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.feature_moment_permission_permanently_denied),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.feature_moment_permission_settings_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                // 앱 설정으로 이동
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.feature_moment_go_to_settings))
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(onClick = onNavigateUp) {
            Text(stringResource(R.string.feature_moment_back_button))
        }
    }
}

/**
 * 카메라 프리뷰와 컨트롤을 담는 컨테이너
 */
@Composable
internal fun CameraPreviewContainer(
    uiState: CameraUiState,
    onCameraReady: () -> Unit,
    onSwitchCamera: () -> Unit,
    onCapturePhoto: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraError: (String) -> Unit,
    onPhotoCaptured: (Uri) -> Unit,
    onCaptureComplete: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 카메라 프리뷰
        CameraPreview(
            lensFacing = uiState.lensFacing,
            shouldCapture = uiState.shouldCapture,
            onCameraReady = onCameraReady,
            onCameraError = onCameraError,
            onPhotoCaptured = onPhotoCaptured,
            onCaptureComplete = onCaptureComplete,
            modifier = Modifier.fillMaxSize()
        )
        
        // 상단 바
        CameraTopBar(
            onNavigateUp = onNavigateUp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
        
        // 하단 컨트롤
        CameraBottomControls(
            isCapturing = uiState.isCapturing,
            onCaptureClick = onCapturePhoto,
            onSwitchCamera = onSwitchCamera,
            onGalleryClick = onGalleryClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
        
        // 촬영 중 로딩 오버레이
        if (uiState.isCapturing) {
            CaptureLoadingOverlay(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * 사진 촬영 중에 표시되는 로딩 오버레이
 */
@Composable
internal fun CaptureLoadingOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(R.string.feature_moment_camera_taking_photo),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}