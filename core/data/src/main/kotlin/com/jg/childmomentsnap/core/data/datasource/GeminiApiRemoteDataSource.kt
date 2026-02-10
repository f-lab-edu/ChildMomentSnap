package com.jg.childmomentsnap.core.data.datasource

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.network.model.gemini.GeminiResponseDto

interface GeminiApiRemoteDataSource {
   suspend fun generateDiary(prompt: String): DataResult<GeminiResponseDto>
}