package com.jg.childmomentsnap.core.model

import java.util.Locale

object DiaryPromptTemplate {

    fun createDiaryPrompt(
        labels: List<String> = emptyList(),
        objects: List<String> = emptyList(),
        emotions: List<String> = emptyList(),
        childName: String? = null,
        locale: Locale = Locale.getDefault()
    ): String {
        val language = when (locale.language) {
            "ko" -> "한국어"
            "ja" -> "일본어"
            "en" -> "영어"
            else -> "영어"
        }

        val labelText = if (labels.isNotEmpty()) {
            "- 감지된 키워드: ${labels.take(5).joinToString(", ")}"
        } else ""

        val objectText = if (objects.isNotEmpty()) {
            "- 감지된 객체: ${objects.take(3).joinToString(", ")}"
        } else ""

        val emotionText = if (emotions.isNotEmpty()) {
            "- 아이 표정/감정: ${emotions.joinToString(", ")}"
        } else ""

        val nameText = childName?.let { "- 아이 이름: $it" } ?: ""

        return """
            당신은 따뜻하고 사랑스러운 육아 일기 작성 도우미입니다.

            [역할]
            부모가 아이의 사진을 보며 일기를 쓰는 것처럼 작성해주세요.

            [조건]
            - 언어: $language
            - 시점: 부모(1인칭) - "오늘 우리 아이가...", "~했어요" 형태
            - 톤: 따뜻하고 애정 어린, 사랑이 느껴지는
            - 분량: 2~4문장

            [사진 정보]
            $labelText
            $objectText
            $emotionText
            $nameText

            [출력 형식]
            일기 내용만 출력하세요. 다른 설명이나 인사말 없이 일기 본문만 작성해주세요.
        """.trimIndent()
    }

    /**
     * VisionFaceEmotion에서 감정 문자열 리스트를 추출합니다.
     */
    fun extractEmotions(faces: List<VisionFaceEmotion>): List<String> {
        if (faces.isEmpty()) return emptyList()

        val emotions = mutableListOf<String>()
        val face = faces.first()

        if (face.joy.isPositive()) emotions.add("기쁨")
        if (face.sorrow.isPositive()) emotions.add("슬픔")
        if (face.anger.isPositive()) emotions.add("분노")
        if (face.surprise.isPositive()) emotions.add("놀람")

        return emotions.ifEmpty { listOf("평온") }
    }

    private fun VisionLikelihood.isPositive(): Boolean {
        return this == VisionLikelihood.LIKELY || this == VisionLikelihood.VERY_LIKELY
    }
}