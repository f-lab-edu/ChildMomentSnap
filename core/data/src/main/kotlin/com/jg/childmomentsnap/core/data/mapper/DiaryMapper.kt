package com.jg.childmomentsnap.core.data.mapper

import com.jg.childmomentsnap.core.model.Diary
import com.jg.childmomentsnap.database.entity.DiaryEntity

internal fun DiaryEntity.toDomain(): Diary {
    return Diary(
        id = id,
        date = date,
        time = time,
        content = content,
        imagePath = imagePath,
        mood = mood,
        bgType = bgType,
        bgValue = bgValue,
        isFavorite = isFavorite,
        location = location,
        isMilestone = isMilestone
    )
}

internal fun Diary.toEntity(): DiaryEntity {
    return DiaryEntity(
        id = id,
        date = date,
        time = time,
        content = content,
        imagePath = imagePath,
        mood = mood,
        bgType = bgType,
        bgValue = bgValue,
        isFavorite = isFavorite,
        location = location,
        isMilestone = isMilestone
    )
}
