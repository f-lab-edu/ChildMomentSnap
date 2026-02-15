package com.jg.childmomentsnap.core.common.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private const val DIARY_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    fun createDiaryDateAndTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(DIARY_DATE_TIME_FORMAT)
        return currentDateTime.format(formatter)
    }

    fun getStartOfDay(date: java.time.LocalDate): String {
        return date.atStartOfDay().format(DateTimeFormatter.ofPattern(DIARY_DATE_TIME_FORMAT))
    }

    fun getEndOfDay(date: java.time.LocalDate): String {
        return date.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern(DIARY_DATE_TIME_FORMAT))
    }

    fun formatYearMonth(yearMonth: java.time.YearMonth): String {
        return yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }
}