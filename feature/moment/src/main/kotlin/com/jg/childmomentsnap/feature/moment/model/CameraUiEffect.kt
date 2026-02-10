package com.jg.childmomentsnap.feature.moment.model

import com.jg.childmomentsnap.core.model.VisionAnalysis

sealed class CameraUiEffect {
    data class ShowError(val message: String): CameraUiEffect()
    data class NavigateToRecording(
        val imageUri: String,
        val visionAnalysisContent: String,
        val visionAnalysis: VisionAnalysis
    ): CameraUiEffect()
}