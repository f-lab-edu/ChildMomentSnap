package com.jg.childmomentsnap.feature.photo.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Android 13+ Photo Picker를 사용한 갤러리 이미지 선택 컴포넌트
 * 
 * 이 컴포넌트는 Android 13+에서 도입된 새로운 Photo Picker API를 사용하여
 * 사용자가 갤러리에서 이미지를 선택할 수 있게 해줍니다.
 * Android 12 이하에서는 기존 ACTION_PICK 방식을 사용합니다.
 * 
 * @param show 갤러리 선택기를 표시할지 여부
 * @param onImageSelected 이미지가 선택되었을 때의 콜백 (null이면 취소)
 * @param onDismiss 갤러리 선택기가 닫혔을 때의 콜백
 */
@Composable
internal fun PhotoPicker(
    show: Boolean,
    onImageSelected: (Uri?) -> Unit,
    onDismiss: () -> Unit
) {
    // Android 13+ Photo Picker 런처
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onImageSelected(uri)
        if (uri == null) {
            onDismiss()
        }
    }
    
    // 레거시 갤러리 선택 런처 (Android 12 이하용)
    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onImageSelected(uri)
        if (uri == null) {
            onDismiss()
        }
    }
    
    // 갤러리 선택기 트리거
    LaunchedEffect(show) {
        if (show) {
            try {
                // Android 13+ Photo Picker 사용 시도
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    // Android 12 이하에서는 레거시 방식 사용
                    legacyGalleryLauncher.launch("image/*")
                }
            } catch (e: Exception) {
                // Photo Picker가 실패하면 레거시 방식으로 폴백
                legacyGalleryLauncher.launch("image/*")
            }
        }
    }
}