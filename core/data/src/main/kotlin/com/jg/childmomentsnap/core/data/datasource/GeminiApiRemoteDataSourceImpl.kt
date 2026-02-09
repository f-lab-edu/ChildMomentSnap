package com.jg.childmomentsnap.core.data.datasource

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.network.model.gemini.GeminiResponseDto
import com.jg.childmomentsnap.core.network.service.GeminiApiService

import javax.inject.Inject


class GeminiApiRemoteDataSourceImpl @Inject constructor(
    private val geminiApiService: GeminiApiService
): GeminiApiRemoteDataSource {
    override suspend fun generateDiary(prompt: String): DataResult<GeminiResponseDto> {
        return geminiApiService.generateContent(prompt = prompt)
    }
}