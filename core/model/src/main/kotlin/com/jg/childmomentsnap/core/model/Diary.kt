package com.jg.childmomentsnap.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Diary(
    val id: Long = 0,
    val date: String,
    val content: String,
    val imagePath: String,
    val bgType: String? = null,
    val bgValue: String? = null,
    val isFavorite: Boolean,
    val emotion: List<EmotionKey> = emptyList()
)
