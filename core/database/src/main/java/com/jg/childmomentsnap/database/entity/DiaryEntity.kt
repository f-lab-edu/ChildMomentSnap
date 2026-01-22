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
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "image_path")
    val imagePath: String?,
    @ColumnInfo(name = "mood") // 기분 (Enum name 저장 ex: "HAPPY")
    val mood: String,
    @ColumnInfo(name = "bgType") // 배경 타입 ("PRESET", "CUSTOM")
    val bgType: String,
    @ColumnInfo(name = "bgValue") // 배경 리소스 ID 또는 파일 경로 (
    val bgValue: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,
    @ColumnInfo(name = "location") // 장소
    val location: String = "",
    @ColumnInfo(name = "is_milestone") // 마일스톤 (별표)
    val isMilestone: Boolean = false,
    @ColumnInfo(name = "emotion") // 감정 (Enum name)
    val emotion: String? = null
)
