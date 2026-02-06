package com.jg.childmomentsnap.core.data.mapper

import com.jg.childmomentsnap.core.model.GeminiAnalysis
import com.jg.childmomentsnap.core.network.model.gemini.GeminiResponseDto

internal fun GeminiResponseDto.toDomain(): GeminiAnalysis {
    return GeminiAnalysis(
        content = analysisResult
    )
}