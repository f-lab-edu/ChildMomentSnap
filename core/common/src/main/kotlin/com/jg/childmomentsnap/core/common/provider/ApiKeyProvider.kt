package com.jg.childmomentsnap.core.common.provider

interface ApiKeyProvider {
    fun getVisionApiKey(): String
    fun getGeminiApiKey(): String
}