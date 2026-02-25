package com.jg.childmomentsnap.feature.feed.viewmodel

import com.jg.childmomentsnap.core.model.Diary
import java.time.LocalDate
import java.time.YearMonth

/**
 * 캘린더 화면의 UI 상태
 *
 * @property yearMonth 현재 표시 중인 연도와 월
 * @property calendarDays 달력 그리드에 표시될 날짜 목록
 * @property selectedDate 사용자가 선택한 날짜
 */
data class FeedUiState(
    val selectedDate: LocalDate? = null,
    val currentMonth: YearMonth = YearMonth.now(),
    val diaries: Map<LocalDate, List<Diary>> = emptyMap(),
    // Expanded state for calendar
    val isCalendarExpanded: Boolean = false,
    // Flattened list of diaries for the feed
    val feedList: List<Diary> = emptyList(),
    // Calendar Data (Pre-calculated in ViewModel)
    val weeklyDays: List<LocalDate> = emptyList(),
    val monthlyDays: List<LocalDate?> = emptyList() // Null represents empty grid cells if any, or just Logic handle
)

sealed interface FeedSideEffect {
    data class NavigateToDetail(val diaryId: Long) : FeedSideEffect
    data object NavigateToCamera : FeedSideEffect
    data class NavigateToWrite(val date: LocalDate) : FeedSideEffect
}
