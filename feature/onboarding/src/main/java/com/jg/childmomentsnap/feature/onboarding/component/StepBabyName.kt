package com.jg.childmomentsnap.feature.onboarding.component

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jg.childmomentsnap.core.ui.permissions.AppPermissions
import com.jg.childmomentsnap.core.ui.permissions.rememberMultiplePermissionsLauncher
import com.jg.childmomentsnap.core.ui.theme.Gray100
import com.jg.childmomentsnap.core.ui.theme.Gray300
import com.jg.childmomentsnap.core.ui.theme.Gray50
import com.jg.childmomentsnap.core.ui.theme.Gray500
import com.jg.childmomentsnap.core.ui.theme.Gray900
import com.jg.childmomentsnap.core.ui.theme.PrimaryAmber
import com.jg.childmomentsnap.feature.onboarding.R
import kotlinx.coroutines.delay


@Composable
internal fun StepBabyName(
    babyName: String,
    profileImageUrl: String? = null,
    onNameChange: (String) -> Unit,
    onProfileImageChange: (String?) -> Unit,
    onNextStep: () -> Unit
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    var showPhotoPicker by remember { mutableStateOf(false) }

    // 갤러리 이미지 선택 런처 (Android 13+ Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { onProfileImageChange(it.toString()) }
    }

    // 레거시 갤러리 선택 런처 (Android 12 이하)
    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onProfileImageChange(it.toString()) }
    }

    val permissionLauncher = rememberMultiplePermissionsLauncher(
        permissions = AppPermissions.Groups.getPhotoPermissions(),
        onAllGranted = {
            showPhotoPicker = true
        },
        onSomeRejected = {
            Toast.makeText(context, "사진을 등록하려면 갤러리 및 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    )

    // 권한 허용 후 갤러리 열기
    LaunchedEffect(showPhotoPicker) {
        if (showPhotoPicker) {
            showPhotoPicker = false
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    legacyGalleryLauncher.launch("image/*")
                }
            } catch (e: Exception) {
                legacyGalleryLauncher.launch("image/*")
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.feature_onboarding_baby_name_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900,
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.feature_onboarding_baby_name_sub_title),
            fontSize = 14.sp,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.feature_onboarding_baby_name_guide_msg),
            fontSize = 14.sp,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 프로필 이미지 등록 영역
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clickable { permissionLauncher() }
            ) {
                // 프로필 기본 배경 또는 이미지
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Gray50)
                        .border(2.dp, Gray100, CircleShape)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUrl != null) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "등록된 프로필 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Face,
                            contentDescription = "기본 프로필",
                            tint = Gray300,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // 우측 하단 카메라 뱃지 아이콘 (5시 방향)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(PrimaryAmber)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "사진 등록",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = babyName,
            onValueChange = onNameChange,
            placeholder = {
                Text(stringResource(R.string.feature_onboarding_baby_name_placeholder), color = Gray300)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Baby Icon",
                    tint = PrimaryAmber
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryAmber,
                unfocusedBorderColor = Gray100,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900,
                cursorColor = PrimaryAmber
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (babyName.isNotBlank()) {
                        onNextStep()
                    }
                }
            )
        )
    }
}