package com.jg.childmomentsnap.core.data.mapper

import com.jg.childmomentsnap.core.model.ChildEmotion
import com.jg.childmomentsnap.core.model.Diary
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
        emotion = emotion
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
        emotion = emotion
    )
}
