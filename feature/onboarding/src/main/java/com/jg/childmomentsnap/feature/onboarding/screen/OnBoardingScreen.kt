package com.jg.childmomentsnap.feature.onboarding.screen

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import com.jg.childmomentsnap.core.ui.theme.Gray100
import com.jg.childmomentsnap.core.ui.theme.PrimaryAmber
import com.jg.childmomentsnap.core.ui.theme.Stone800
import com.jg.childmomentsnap.feature.onboarding.R
import com.jg.childmomentsnap.feature.onboarding.component.StepBabyName
import com.jg.childmomentsnap.feature.onboarding.component.StepBirthDate
import com.jg.childmomentsnap.feature.onboarding.component.StepRoleSelection
import com.jg.childmomentsnap.feature.onboarding.viewmodel.OnBoardingViewModel
import com.jg.childmomentsnap.feature.onboarding.viewmodel.OnboardingConstants
import com.jg.childmomentsnap.feature.onboarding.viewmodel.OnboardingSideEffect
import com.jg.childmomentsnap.feature.onboarding.viewmodel.UiState

@Composable
internal fun OnboardingRoute(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    onCompleted: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is OnboardingSideEffect.NavigateToHome -> onCompleted()
                    is OnboardingSideEffect.ErrorMessage -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.loadInitRoles()
        }
    }

    OnBoardingScreen(
        uiState = uiState.value,
        step = uiState.value.step,
        onRoleSelect = viewModel::onRoleSelect,
        onCustomRoleChange = viewModel::onCustomRoleChange,
        onNameChange = viewModel::onNameChange,
        onDateChange = viewModel::onBirthDateChange,
        onPregnantChange = viewModel::onPregnantChange,
        onNext = viewModel::onNext,
        onComplete = viewModel::onCompleteOnboarding,
        onProfileImageChange = viewModel::onProfileImageChange,
        onBackPressed = {}
    )
}

@Composable
private fun OnBoardingScreen(
    uiState: UiState,
    step: Int,
    onRoleSelect:(String) -> Unit,
    onCustomRoleChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onPregnantChange: (Boolean) -> Unit,
    onProfileImageChange: (String?) -> Unit,
    onNext: (Int) -> Unit,
    onComplete: () -> Unit,
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
        }, bottomBar = {
            Button(
                onClick = {
                    when (step) {
                        OnboardingConstants.STEP_ONE_SET_ROLE -> {
                            onNext(OnboardingConstants.STEP_TWO_SET_NAME)
                        }

                        OnboardingConstants.STEP_TWO_SET_NAME -> {
                            onNext(OnboardingConstants.STEP_THREE_SET_BIRTHDAY)
                        }
                        OnboardingConstants.STEP_THREE_SET_BIRTHDAY -> {
                            onComplete()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                enabled = uiState.isNextEnable,
            ) {
                Text (
                    text = if (step == OnboardingConstants.STEP_THREE_SET_BIRTHDAY) stringResource(R.string.feature_onboarding_role_start) else stringResource(R.string.feature_onboarding_role_next)
                )
            }
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
                        selectedRole = uiState.roleUiState.selectedRole,
                        onRoleSelect = onRoleSelect,
                        customRole = uiState.roleUiState.otherRole ?: "",
                        onCustomRoleChange = onCustomRoleChange
                    )
                }

                OnboardingConstants.STEP_TWO_SET_NAME -> {
                    StepBabyName(
                        babyName = uiState.babyName,
                        profileImageUrl = uiState.profileImageUrl,
                        onNameChange = onNameChange,
                        onProfileImageChange = onProfileImageChange,
                        onNextStep = {
                            onNext(OnboardingConstants.STEP_THREE_SET_BIRTHDAY)
                        }
                    )
                }

                OnboardingConstants.STEP_THREE_SET_BIRTHDAY -> {
                    StepBirthDate(
                        babyName = uiState.babyName,
                        birthDate = uiState.birthDay,
                        onDateChange = onDateChange,
                        isPregnant = uiState.isPregnant,
                        onPregnantChange = onPregnantChange,
                        onComplete = onComplete
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