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
            val isRateLimit = e.message?.contains("429") == true || e.message?.contains("Too Many Requests", ignoreCase = true) == true
            if (isRateLimit) {
                DataResult.Fail(
                    code = 429,
                    message = "현재 사용자가 많아 AI 서버 응답이 지연되고 있습니다. 잠시 후 다시 시도해 주세요.",
                    throwable = e
                )
            } else {
                DataResult.Fail(code = -1, message = e.message, throwable = e)
            }
        }
    }
}
