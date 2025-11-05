package com.jg.childmomentsnap.feature.photo.model

sealed class CameraUiEffect {
    data class ShowError(val message: String): CameraUiEffect()
}
