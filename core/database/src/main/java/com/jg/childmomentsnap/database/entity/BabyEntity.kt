package com.jg.childmomentsnap.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "baby_table",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index(value = ["user_id"])
    ]
)
data class BabyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "user_id")
    val userId: Long = 0L,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("birth_date")
    val birthDate: String,
    @ColumnInfo("is_pregnant")
    val isPregnant: Boolean,
    @ColumnInfo("profile_image_url")
    val profileImgUrl: String? = null
)
