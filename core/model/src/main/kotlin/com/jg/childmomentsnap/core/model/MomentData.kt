package com.jg.childmomentsnap.core.model

import kotlinx.serialization.Serializable

@Serializable
data class MomentData(
    val analysis: VisionAnalysis,
    val transcription: String
)
