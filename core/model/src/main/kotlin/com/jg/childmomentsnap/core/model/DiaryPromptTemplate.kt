package com.jg.childmomentsnap.core.model

import java.util.Locale

object DiaryPromptTemplate {

    fun createDiaryPrompt(
        keywords: List<String>,      // Vision API 결과
        childName: String? = "My Baby",   // 아이 이름 (선택)
        locale: Locale = Locale.getDefault()
    ): String {
        val language = when (locale.language) {
            "ko" -> "한국어"
            "ja" -> "일본어"
            "en" -> "영어"
            else -> "영어"
        }

        val keywordText = keywords.take(5).joinToString(", ")
        val nameText = childName?.let { "아이 이름: $it" } ?: ""

        return """
            당신은 따뜻하고 사랑스러운 육아 일기 작성 도우미입니다.
            
            다음 정보를 바탕으로 아이의 하루를 기록하는 짧고 따뜻한 일기를 작성해주세요.
            
            [조건]
            - 언어: $language
            - 분량: 2~3문장
            - 톤: 따뜻하고 애정 어린 부모의 시선
            - 시점: 부모가 아이를 바라보며 쓰는 일기
            $nameText
            
            [사진에서 감지된 키워드]
            $keywordText
            
            [출력 형식]
            일기 내용만 출력하세요. 다른 설명은 필요 없습니다.
        """.trimIndent()
    }
}