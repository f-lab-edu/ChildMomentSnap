package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.domain.repository.PhotoRepository
import com.jg.childmomentsnap.core.domain.repository.VoiceRepository
import com.jg.childmomentsnap.core.model.MomentData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File
import javax.inject.Inject

class ProcessMomentUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val voiceRepository: VoiceRepository
) {
    suspend operator fun invoke(imageBytes: ByteArray, voiceFile: File): Result<MomentData> = coroutineScope {
        try {
            val analysisDeferred = async { photoRepository.analyzeImage(imageBytes) }
            val transcriptionDeferred = async { voiceRepository.transcribe(voiceFile) }

            val analysis = analysisDeferred.await()
            val transcription = transcriptionDeferred.await()

            Result.success(
                MomentData(
                    analysis = analysis,
                    transcription = transcription
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
