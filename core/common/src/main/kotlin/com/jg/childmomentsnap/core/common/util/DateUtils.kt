package com.jg.childmomentsnap.core.common.util

import java.time.LocalDate
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

    private fun parseDate(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(DIARY_DATE_TIME_FORMAT))
    }

    fun parseDateOrNull(dateString: String): LocalDateTime? {
        return try {
            parseDate(dateString)
        } catch (e: Exception) {
            null
        }
    }
}