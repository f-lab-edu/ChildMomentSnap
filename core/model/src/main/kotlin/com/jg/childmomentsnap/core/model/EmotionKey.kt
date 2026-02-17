package com.jg.childmomentsnap.core.model

/**
 * Vision API 기반 감정 분석 키
 *
 * DB에는 enum의 name (JOY, SORROW 등)이 콤마 구분 문자열로 저장됩니다.
 */
enum class EmotionKey {
    JOY,
    SORROW,
    ANGER,
    SURPRISE,
    CALM
}
