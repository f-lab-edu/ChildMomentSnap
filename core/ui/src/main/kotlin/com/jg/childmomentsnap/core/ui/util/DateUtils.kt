package com.jg.childmomentsnap.core.ui.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private const val DIARY_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    fun createDiaryDateAndTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(DIARY_DATE_TIME_FORMAT)
        return currentDateTime.format(formatter)
    }
}