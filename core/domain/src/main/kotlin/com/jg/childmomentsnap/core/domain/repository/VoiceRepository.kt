package com.jg.childmomentsnap.core.domain.repository

import java.io.File

interface VoiceRepository {
    suspend fun transcribe(voiceFile: File): String
}
