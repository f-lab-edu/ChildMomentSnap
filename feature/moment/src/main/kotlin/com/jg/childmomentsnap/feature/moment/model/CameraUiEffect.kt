package com.jg.childmomentsnap.feature.moment.model

sealed class CameraUiEffect {
    data class ShowError(val message: String): CameraUiEffect()
}
