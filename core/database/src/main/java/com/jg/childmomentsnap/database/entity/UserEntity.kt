package com.jg.childmomentsnap.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "role_type")
    val roleType: String,
    @ColumnInfo(name = "custom_role_name")
    val customRoleName: String? = null,
    @ColumnInfo(name = "profile_image_url")
    val profileImageUrl: String? = null,
    @ColumnInfo(name = "baby_name")
    val babyName: String,
    @ColumnInfo(name = "birth_day")
    val birthDay: String,
    @ColumnInfo(name = "is_pregnant")
    val isPregnant: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: String
)