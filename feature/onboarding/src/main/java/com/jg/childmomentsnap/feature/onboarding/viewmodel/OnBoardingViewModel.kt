package com.jg.childmomentsnap.feature.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import com.jg.childmomentsnap.feature.onboarding.R
import com.jg.childmomentsnap.feature.onboarding.model.RoleItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


data class UiState(
    val step: Int,
    val roleItems: List<RoleItem>,
    val selectedRole: String,
) {
    companion object {
        val EMPTY = UiState(step = 0, roleItems = emptyList(), selectedRole = "")
    }
}

object OnboardingConstants {
    val roles = listOf(
        RoleItem(id = "mom", emoji = "\uD83D\uDC69", titleResId = R.string.feat_onboarding_role_mom),
        RoleItem(id = "dad", emoji = "\uD83D\uDC68", titleResId = R.string.feat_onboarding_role_dad),
        RoleItem(id = "grandma", emoji = "\uD83D\uDC75", titleResId = R.string.feat_onboarding_role_grandma),
        RoleItem(id = "grandpa", emoji = "\uD83D\uDC74", titleResId = R.string.feat_onboarding_role_grandpa),
        RoleItem(id = "other", emoji = "", titleResId =  R.string.feat_onboarding_role_other)
    )

    val STEP_ONE_SET_ROLE = 1
    val STEP_TWO_SET_NAME = 2
    val STEP_THREE_SET_BIRTHDAY = 3
    val STEP_TOTAL_COUNT = 3
}

class OnBoardingViewModel @Inject constructor(

): ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.EMPTY)
    val uiState = _uiState.asStateFlow()

    init {
        loadInitRoles()
    }

    fun loadInitRoles() {
        _uiState.update { current ->
            current.copy(
                step = OnboardingConstants.STEP_ONE_SET_ROLE,
                roleItems = OnboardingConstants.roles
            )
        }
    }
}