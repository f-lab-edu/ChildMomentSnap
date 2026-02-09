package com.jg.childmomentsnap.core.network.service

import com.google.ai.client.generativeai.GenerativeModel
import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.network.model.gemini.GeminiResponseDto
import javax.inject.Inject

class GeminiApiServiceImpl @Inject constructor(
    private val generativeModel: GenerativeModel
) : GeminiApiService {

    override suspend fun generateContent(prompt: String): DataResult<GeminiResponseDto> {
        return try {
            val response = generativeModel.generateContent(prompt)
            DataResult.Success(GeminiResponseDto(analysisResult = response.text ?: ""))
        } catch (e: Exception) {
            DataResult.Fail(code = -1, message = e.message, throwable = e)
        }
    }
}
