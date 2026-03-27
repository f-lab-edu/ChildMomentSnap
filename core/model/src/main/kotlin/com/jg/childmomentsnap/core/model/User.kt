package com.jg.childmomentsnap.core.model

import com.jg.childmomentsnap.core.model.enums.RoleType
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val roleType: RoleType,
    val babyName: String,
    val birthDay: String,
    val isPregnant: Boolean,
    val customRoleName: String? = null,
    val profileImageUrl: String? = null,
)
