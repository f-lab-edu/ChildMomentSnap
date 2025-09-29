package com.jg.childmomentsnap.feature.photo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 네비게이션을 포함한 카메라 화면의 상단 바
 */
@Composable
internal fun CameraTopBar(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Transparent
                    )
                )
            )
            .padding(16.dp)
            .systemBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 뒤로가기 버튼
        IconButton(
            onClick = onNavigateUp,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로 가기",
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 제목
        Text(
            text = "사진 촬영",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 카메라 작업을 위한 하단 컨트롤
 */
@Composable
internal fun CameraBottomControls(
    isCapturing: Boolean,
    onCaptureClick: () -> Unit,
    onSwitchCamera: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                Color.Black.copy(alpha = 0.4f),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 갤러리 버튼
        IconButton(
            onClick = onGalleryClick,
            enabled = !isCapturing,
            modifier = Modifier
                .background(
                    if (isCapturing) Color.Gray.copy(alpha = 0.3f) else Color.Transparent,
                    CircleShape
                )
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "갤러리",
                tint = if (isCapturing) Color.Gray else Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 촬영 버튼
        IconButton(
            onClick = onCaptureClick,
            enabled = !isCapturing,
            modifier = Modifier
                .size(72.dp)
                .background(
                    if (isCapturing) Color.Gray else Color.White,
                    CircleShape
                )
        ) {
            if (isCapturing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "촬영",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 카메라 전환 버튼
        IconButton(
            onClick = onSwitchCamera,
            enabled = !isCapturing,
            modifier = Modifier
                .background(
                    if (isCapturing) Color.Gray.copy(alpha = 0.3f) else Color.Transparent,
                    CircleShape
                )
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "카메라 전환",
                tint = if (isCapturing) Color.Gray else Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}