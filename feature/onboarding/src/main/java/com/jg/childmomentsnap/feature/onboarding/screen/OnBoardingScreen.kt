package com.jg.childmomentsnap.feature.onboarding.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.jg.childmomentsnap.core.model.enums.RoleType
import com.jg.childmomentsnap.core.ui.theme.DarkAccent
import com.jg.childmomentsnap.core.ui.theme.Gray100
import com.jg.childmomentsnap.core.ui.theme.Gray500
import com.jg.childmomentsnap.core.ui.theme.Gray900
import com.jg.childmomentsnap.core.ui.theme.PrimaryAmber
import com.jg.childmomentsnap.core.ui.theme.SelectedBg
import com.jg.childmomentsnap.core.ui.theme.Stone800
import com.jg.childmomentsnap.feature.onboarding.R
import com.jg.childmomentsnap.feature.onboarding.component.RoleCard
import com.jg.childmomentsnap.feature.onboarding.model.RoleItem
import com.jg.childmomentsnap.feature.onboarding.viewmodel.OnBoardingViewModel
import com.jg.childmomentsnap.feature.onboarding.viewmodel.OnboardingConstants
import com.jg.childmomentsnap.feature.onboarding.viewmodel.OnboardingConstants.roles
import com.jg.childmomentsnap.feature.onboarding.viewmodel.UiState

@Composable
internal fun OnboardingRoute(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    onCompleted: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.loadInitRoles()
        }
    }

    OnBoardingScreen(
        uiState = uiState.value,
        step = uiState.value.step,
        onBackPressed = {}
    )
}

@Composable
private fun OnBoardingScreen(
    uiState: UiState,
    step: Int,
    onBackPressed: () -> Unit,
) {
    val progress by animateFloatAsState(
        targetValue = uiState.step.toFloat() / OnboardingConstants.STEP_TOTAL_COUNT.toFloat(),
        animationSpec = tween(500),
        label = "progressAnimation"
    )

    Scaffold(
        topBar = {
            StepProgressTopBar(
                step = uiState.step,
                progress = progress,
                onBackPressed = onBackPressed
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (step) {
                OnboardingConstants.STEP_ONE_SET_ROLE -> {
                    StepRoleSelection(
                        roles = uiState.roleUiState.roleItems,
                        selectedRole = null,
                        onRoleSelect = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun StepProgressTopBar(
    step: Int,
    progress: Float,
    onBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = onBackPressed
        ) {
            if(step != OnboardingConstants.STEP_ONE_SET_ROLE) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Stone800
                )
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = PrimaryAmber,
            trackColor = Gray100
        )
        Text(
            text = stringResource(R.string.feature_onboarding_role_topbar_step_count, step),
            color = PrimaryAmber,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 8.dp, start = 24.dp)
        )
    }
}

@Composable
private fun StepRoleSelection(
    modifier: Modifier = Modifier,
    roles: List<RoleItem>,
    selectedRole: String?,
    onRoleSelect: (String) -> Unit
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.feature_onboarding_role_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900,
            lineHeight = 34.sp
        )

        Text(
            text = stringResource(R.string.feature_onboarding_role_guide_msg),
            fontSize = 14.sp,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                roles.take(2).forEach { role ->
                    RoleCard(
                        modifier = Modifier.weight(1f),
                        role = role,
                        isSelected = selectedRole == role.id,
                        onClick = { onRoleSelect(role.id) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                roles.drop(2).forEach { role ->
                    if (role.emoji.isNotEmpty()) {
                        RoleCard(
                            modifier = Modifier.weight(1f),
                            role = role,
                            isSelected = selectedRole == role.id,
                            onClick = { onRoleSelect(role.id) }
                        )
                    }
                }
            }

            // 기타 직접 입력
            val isOtherSelected = selectedRole == RoleType.OTHER.role
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isOtherSelected) SelectedBg.copy(alpha = 0.5f) else Color.White)
                    .border(
                        2.dp,
                        if (isOtherSelected) PrimaryAmber else Gray100,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onRoleSelect(RoleType.OTHER.role) }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👤", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.feature_onboarding_role_other_input),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isOtherSelected) DarkAccent else Color.DarkGray
                        )
                    }
                    if (isOtherSelected) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(PrimaryAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
