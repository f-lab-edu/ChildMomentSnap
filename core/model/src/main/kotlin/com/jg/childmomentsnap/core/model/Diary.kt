package com.jg.childmomentsnap.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Diary(
    val id: Long = 0,
    val date: String,
    val time: String,
    val content: String,
    val imagePath: String?,
    val mood: String,
    val bgType: String,
    val bgValue: String,
    val isFavorite: Boolean,
    val location: String = "",
    val isMilestone: Boolean = false,
    val emotion: ChildEmotion? = null
)
