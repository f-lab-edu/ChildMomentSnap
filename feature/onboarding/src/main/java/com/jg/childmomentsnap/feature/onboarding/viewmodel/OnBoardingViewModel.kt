package com.jg.childmomentsnap.feature.onboarding.viewmodel

import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import com.jg.childmomentsnap.core.model.enums.RoleType
import com.jg.childmomentsnap.feature.onboarding.R
import com.jg.childmomentsnap.feature.onboarding.model.RoleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.usecase.SetUserUseCase
import com.jg.childmomentsnap.core.model.User


data class UiState(
    val step: Int,
    val roleUiState: RoleUiState,
    val babyName: String,
    val profileImageUrl: String? = null,
    val birthDay: String,
    val isPregnant: Boolean,
    val isNextEnable: Boolean
) {
    companion object {
        val EMPTY = UiState(step = 0, roleUiState = RoleUiState.EMPTY, babyName = "", profileImageUrl = null, isNextEnable = false, birthDay = "", isPregnant = false)
    }
}

sealed class OnboardingSideEffect {
    object NavigateToHome : OnboardingSideEffect()
    class ErrorMessage(val message: String) : OnboardingSideEffect()
}

data class RoleUiState(
    val selectedRole: String,
    val roleItems: List<RoleItem>,
    val otherRole: String? = null
) {
    companion object {
        val EMPTY = RoleUiState(selectedRole = "", roleItems = emptyList())
    }
}

object OnboardingConstants {
    val roles = listOf(
        RoleItem(roleName = "mom", emoji = "\uD83D\uDC69", titleResId = R.string.feature_onboarding_role_mom),
        RoleItem(roleName = "dad", emoji = "\uD83D\uDC68", titleResId = R.string.feature_onboarding_role_dad),
        RoleItem(roleName = "grandma", emoji = "\uD83D\uDC75", titleResId = R.string.feature_onboarding_role_grandma),
        RoleItem(roleName = "grandpa", emoji = "\uD83D\uDC74", titleResId = R.string.feature_onboarding_role_grandpa),
        RoleItem(roleName = "other", emoji = "", titleResId =  R.string.feature_onboarding_role_other)
    )

    val STEP_ONE_SET_ROLE = 1
    val STEP_TWO_SET_NAME = 2
    val STEP_THREE_SET_BIRTHDAY = 3
    val STEP_TOTAL_COUNT = 3
}

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val setUserUseCase: SetUserUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.EMPTY)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<OnboardingSideEffect>()
    val uiEffect = _uiEffect.asSharedFlow()


    fun loadInitRoles() {
        _uiState.update { current ->
            current.copy(
                step = OnboardingConstants.STEP_ONE_SET_ROLE,
                roleUiState = current.roleUiState.copy(
                    roleItems = OnboardingConstants.roles
                )
            )
        }
    }

    fun onRoleSelect(role: String) {
        _uiState.update { current ->
            current.copy(
                roleUiState = current.roleUiState.copy(
                    selectedRole = role
                ),
                isNextEnable = true,
            )
        }
    }

    fun onCustomRoleChange(role: String) {
        _uiState.update { current ->
            current.copy(
                roleUiState = current.roleUiState.copy(
                    selectedRole = RoleType.OTHER.role,
                    otherRole = role
                ),
                isNextEnable = role.isNotEmpty(),
            )
        }
    }

    fun onNameChange(babyName: String) {
        _uiState.update { current ->
            current.copy(
                babyName = babyName,
                isNextEnable = babyName.isNotEmpty()
            )
        }
    }

    fun onProfileImageChange(uri: String?) {
        _uiState.update { current ->
            current.copy(profileImageUrl = uri)
        }
    }

    fun onNext(step: Int) {
        _uiState.update { current ->
            current.copy(
                step = step,
                isNextEnable = false
            )
        }
    }

    fun onBirthDateChange(date: String) {
        _uiState.update { current ->
            current.copy(
                birthDay = date,
                isNextEnable = date.isNotEmpty()
            )
        }
    }

    fun onPregnantChange(isPregnant: Boolean) {
        _uiState.update { current ->
            current.copy(
                isPregnant = isPregnant,
                birthDay = if (isPregnant) "" else current.birthDay,
                isNextEnable = isPregnant || current.birthDay.isNotEmpty()
            )
        }
    }

    fun onCompleteOnboarding() {
        viewModelScope.launch {
            val result = setUserUseCase.invoke(
                user = User(
                    roleType = RoleType.fromRole(uiState.value.roleUiState.selectedRole),
                    customRoleName = uiState.value.roleUiState.otherRole,
                    babyName = uiState.value.babyName,
                    birthDay = uiState.value.birthDay,
                    isPregnant = uiState.value.isPregnant
                )
            )
            when (result) {
                is DomainResult.Success -> {
                    if (result.data) {
                        _uiEffect.emit(OnboardingSideEffect.NavigateToHome)
                    }
                }
                is DomainResult.Fail -> {
                    _uiEffect.emit(OnboardingSideEffect.ErrorMessage(result.error))
                }
            }
        }
    }
}