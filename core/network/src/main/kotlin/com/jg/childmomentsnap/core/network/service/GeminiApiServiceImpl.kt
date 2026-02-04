package com.jg.childmomentsnap.core.network.service

import com.google.ai.client.generativeai.GenerativeModel
import com.jg.childmomentsnap.core.network.model.gemini.GeminiResponseDto
import javax.inject.Inject
import javax.inject.Named

class GeminiApiServiceImpl @Inject constructor(
    private val generativeModel: GenerativeModel
) : GeminiApiService {

    override suspend fun generateContent(prompt: String): GeminiResponseDto {
        return try {
            val response = generativeModel.generateContent(prompt)
            GeminiResponseDto(
                analysisResult = response.text?.trim() ?: ""
            )
        } catch (e: Exception) {
            throw e
        }
    }
}
