package com.jg.childmomentsnap.feature.splash.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.usecase.IsFirstLaunchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  26.03.11
 *  - 현재는 간단한 로직인지만, 추후 확장을 위해 Splash 모듈로 생성
 */

data class UiState(
    val isFirstLaunch: Boolean = false
) {
    companion object {
        val EMPTY = UiState(isFirstLaunch = false)
    }
}

sealed class SplashSideEffect {
    object NavigateToOnboarding : SplashSideEffect()
    object NavigateToHome : SplashSideEffect()
    data class ErrorMessage(val error: String): SplashSideEffect()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isFirstLaunchUseCase: IsFirstLaunchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow( UiState.EMPTY)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SplashSideEffect>()
    val uiEffect = _uiEffect.asSharedFlow()


    fun checkFirstLaunch() {
        viewModelScope.launch {
            when (val result = isFirstLaunchUseCase.invoke()) {
                is DomainResult.Success -> {
                    if (result.data) {
                        _uiEffect.emit(SplashSideEffect.NavigateToOnboarding)
                    } else {
                        _uiEffect.emit(SplashSideEffect.NavigateToHome)
                    }
                }
                is DomainResult.Fail -> {
                    _uiEffect.emit(SplashSideEffect.ErrorMessage(result.error))
                }
            }
        }
    }
}