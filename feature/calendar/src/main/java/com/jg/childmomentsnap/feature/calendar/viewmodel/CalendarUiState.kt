package com.jg.childmomentsnap.feature.calendar.viewmodel

import java.time.LocalDate
import java.time.YearMonth

/**
 * 캘린더 화면의 UI 상태
 *
 * @property yearMonth 현재 표시 중인 연도와 월
 * @property calendarDays 달력 그리드에 표시될 날짜 목록
 * @property selectedDate 사용자가 선택한 날짜
 */
data class CalendarUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedDate: LocalDate? = null
)

/**
 * 달력 그리드의 개별 날짜 아이템 정보
 *
 * @property date 해당 날짜
 * @property isCurrentMonth 현재 표시 중인 월에 속하는지 여부 (이전/다음 달 날짜 구분용)
 * @property isToday 오늘 날짜인지 여부
 */
data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val content: String? = null
)
