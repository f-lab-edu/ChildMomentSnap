package com.jg.childmomentsnap.feature.calendar.viewmodel

import com.jg.childmomentsnap.core.model.Diary
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val diaries: Map<LocalDate, List<Diary>> = emptyMap(),
    val selectedDate: LocalDate? = null,
    val isBottomSheetVisible: Boolean = false,
    val bottomSheetDiaries: List<Diary> = emptyList()
)

sealed interface CalendarSideEffect {
    data class ShowWriteSelectionDialog(val date: LocalDate) : CalendarSideEffect
    data class NavigateToDetail(val diaryId: Long) : CalendarSideEffect
    data object NavigateToCamera : CalendarSideEffect
    data class NavigateToWrite(val date: LocalDate) : CalendarSideEffect
}
