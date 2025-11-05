package com.jg.childmomentsnap.feature.photo.screen

import android.widget.Toast
import com.jg.childmomentsnap.feature.photo.CameraViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.jg.childmomentsnap.core.ui.permissions.hasAllPermissions
import com.jg.childmomentsnap.core.ui.permissions.AppPermissions
import com.jg.childmomentsnap.feature.photo.model.CameraUiEffect

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
fun CameraRoute(
    onBackClick: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 뒤로가기 임시
    BackHandler(onBack = onBackClick)
    
    // 화면 진입 시 권한 확인
    LaunchedEffect(Unit) {
        val hasAllPermissions = context.hasAllPermissions(AppPermissions.Groups.getPhotoPermissions())
        viewModel.updatePermissionState(hasAllPermissions)
    }
    
    // 앱이 포그라운드로 돌아올 때 권한 재확인
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val hasAllPermissions = context.hasAllPermissions(AppPermissions.Groups.getPhotoPermissions())
                viewModel.updatePermissionState(hasAllPermissions)
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // 화면을 떠날 때 카메라 상태 초기화
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetCameraState()
        }
    }

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
    
    CameraScreen(
        uiState = uiState,
        onPermissionResult = viewModel::onPermissionResult,
        onCameraReady = viewModel::onCameraReady,
        onSwitchCamera = viewModel::switchCamera,
        onCapturePhoto = viewModel::capturePhoto,
        onGalleryClick = viewModel::openGallery,
        onImageSelected = viewModel::onImageSelected,
        onDismissGalleryPicker = viewModel::dismissGalleryPicker,
        onConfirmImage = {
            viewModel.confirmSelectedImage(context)
        },
        onCancelImage = viewModel::cancelSelectedImage,
        onClearError = viewModel::clearError,
        onCameraError = viewModel::onCameraError,
        onPhotoCaptured = viewModel::onPhotoCaptured,
        onCaptureComplete = viewModel::onCaptureComplete,
        onConfirmCapturedImage = {
            viewModel.confirmCapturedImage(context)
        },
        onRetakeCapturedPhoto = viewModel::retakeCapturedPhoto,
        onNavigateUp = onNavigateUp,
        modifier = modifier
    )
}