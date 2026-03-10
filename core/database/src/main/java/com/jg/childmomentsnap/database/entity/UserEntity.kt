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
    val customRoleName: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String
)