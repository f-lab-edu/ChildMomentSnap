package com.jg.childmomentsnap.core.data.mapper

import com.jg.childmomentsnap.core.common.util.DateUtils
import com.jg.childmomentsnap.core.model.User
import com.jg.childmomentsnap.core.model.enums.RoleType
import com.jg.childmomentsnap.database.entity.UserEntity

internal fun UserEntity.toDomain(): User {
    return User(
        id = id,
        roleType = RoleType.fromRole(roleType),
        customRoleName = customRoleName,
        profileImageUrl = profileImageUrl,
        babyName = babyName,
        birthDay = birthDay,
        isPregnant = isPregnant
    )
}

internal fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        roleType = roleType.role,
        customRoleName = customRoleName,
        profileImageUrl = profileImageUrl,
        babyName = babyName,
        birthDay = birthDay,
        isPregnant = isPregnant,
        createdAt = DateUtils.createDiaryDateAndTime()
    )
}