package com.jg.childmomentsnap.core.model.enums

enum class RoleType(val role: String) {
    MOM("mom"),
    DAD("dad"),
    GRANDMA("grandma"),
    GRANDPA("grandpa"),
    OTHER("other");

    companion object {
        fun fromRole(role: String): RoleType {
            return entries.find { it.role == role } ?: OTHER
        }
    }
}