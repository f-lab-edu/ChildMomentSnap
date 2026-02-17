package com.jg.childmomentsnap.core.ui.util

import com.jg.childmomentsnap.core.model.EmotionKey
import com.jg.childmomentsnap.core.ui.R


object EmotionChipResources {

    private val emotionChipMap: Map<EmotionKey, Int> = mapOf(
        EmotionKey.JOY to R.string.emotion_chip_joy,
        EmotionKey.SORROW to R.string.emotion_chip_sorrow,
        EmotionKey.ANGER to R.string.emotion_chip_anger,
        EmotionKey.SURPRISE to R.string.emotion_chip_surprise,
        EmotionKey.CALM to R.string.emotion_chip_calm
    )

    fun getEmotionChipResId(key: EmotionKey): Int? {
        return emotionChipMap[key]
    }

    fun getEmotionChipResIds(keys: List<EmotionKey>): List<Int> {
        return keys.mapNotNull { emotionChipMap[it] }
    }
}
