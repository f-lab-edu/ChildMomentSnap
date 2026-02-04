package com.jg.childmomentsnap.core.network.service

import com.jg.childmomentsnap.core.network.model.gemini.GeminiResponseDto

interface GeminiApiService {
    suspend fun generateContent(prompt: String): GeminiResponseDto
}
