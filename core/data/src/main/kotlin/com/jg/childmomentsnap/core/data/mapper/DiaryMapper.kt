package com.jg.childmomentsnap.core.data.mapper

import com.jg.childmomentsnap.core.model.Diary
import com.jg.childmomentsnap.core.model.EmotionKey
import com.jg.childmomentsnap.database.entity.DiaryEntity

internal fun DiaryEntity.toDomain(): Diary {
    return Diary(
        id = id,
        date = date,
        content = content,
        imagePath = imagePath,
        bgType = bgType,
        bgValue = bgValue,
        isFavorite = isFavorite,
        emotion = emotion?.split(",")
            ?.filter { it.isNotBlank() }
            ?.mapNotNull { runCatching { EmotionKey.valueOf(it) }.getOrNull() }
            ?: emptyList()
    )
}

internal fun Diary.toEntity(): DiaryEntity {
    return DiaryEntity(
        id = id,
        date = date,
        content = content,
        imagePath = imagePath,
        bgType = bgType,
        bgValue = bgValue,
        isFavorite = isFavorite,
        emotion = emotion.takeIf { it.isNotEmpty() }?.joinToString(",") { it.name }
    )
}

