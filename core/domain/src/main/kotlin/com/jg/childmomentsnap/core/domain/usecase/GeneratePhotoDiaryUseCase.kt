package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import com.jg.childmomentsnap.core.domain.repository.PhotoRepository
import com.jg.childmomentsnap.core.model.DiaryPromptTemplate
import com.jg.childmomentsnap.core.model.GeminiAnalysis
import javax.inject.Inject

interface GeneratePhotoDiaryUseCase {
    suspend operator fun invoke(imageBytes: ByteArray): DomainResult<GeminiAnalysis, String>
}

class GeneratePhotoDiaryUseCaseImpl @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val diaryRepository: DiaryRepository
) : GeneratePhotoDiaryUseCase {

    override suspend fun invoke(imageBytes: ByteArray): DomainResult<GeminiAnalysis, String> {
        val visionResult = photoRepository.analyzeImage(imageBytes)

        val labels = visionResult.labels.map { it.description }
        val objects = visionResult.objects.map { it.name }
        val emotions = DiaryPromptTemplate.extractEmotions(visionResult.faces)

        val prompt = DiaryPromptTemplate.createDiaryPrompt(
            labels = labels,
            objects = objects,
            emotions = emotions
        )

        return when (val result = diaryRepository.generateGeminiDairy(prompt)) {
            is DataResult.Success -> DomainResult.Success(
                GeminiAnalysis(
                    content = result.data,
                    visionAnalysis = visionResult
                )
            )
            is DataResult.Fail -> DomainResult.Fail(result.message ?: "")
        }
    }
}