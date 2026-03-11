package com.jg.childmomentsnap.core.data.mapper

import com.jg.childmomentsnap.core.model.Baby
import com.jg.childmomentsnap.database.entity.BabyEntity

internal fun BabyEntity.toDomain(): Baby {
    return Baby(
        id = id,
        userId = userId,
        name = name,
        birthDate = birthDate,
        isPregnant = isPregnant,
        profileImageUrl = profileImgUrl
    )
}

internal fun Baby.toEntity(): BabyEntity {
    return BabyEntity(
        id = id,
        userId = userId,
        name = name,
        birthDate = birthDate,
        isPregnant = isPregnant,
        profileImgUrl = profileImageUrl
    )
}