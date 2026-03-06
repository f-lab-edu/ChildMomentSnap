package com.jg.childmomentsnap.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_table")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "image_path")
    val imagePath: String,
    @ColumnInfo(name = "bgType") // 배경 타입 ("PRESET", "CUSTOM") (추후)
    val bgType: String? = null,
    @ColumnInfo(name = "bgValue") // 배경 리소스 ID 또는 파일 경로 (추후)
    val bgValue: String? = null,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "location") // 장소
    val location: String? = null,
    @ColumnInfo(name = "emotion") // 감정
    val emotion: String? = null
)
