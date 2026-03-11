package com.jg.childmomentsnap.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Baby(
    val id: Long = 0,
    val userId: Long = 0,
    val name: String,
    val birthDate: String,
    val isPregnant: Boolean = false,
    val profileImageUrl: String? = null,
)
