package com.jg.childmomentsnap.feature.calendar.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        updateCalendarDays(_uiState.value.yearMonth)
    }

    fun onPreviousMonthClick() {
        _uiState.update { currentState ->
            val prevMonth = currentState.yearMonth.minusMonths(1)
            updateCalendarDays(prevMonth)
            currentState.copy(yearMonth = prevMonth)
        }
    }

    fun onNextMonthClick() {
        _uiState.update { currentState ->
            val nextMonth = currentState.yearMonth.plusMonths(1)
            updateCalendarDays(nextMonth)
            currentState.copy(yearMonth = nextMonth)
        }
    }

    fun onDateClick(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    private fun updateCalendarDays(yearMonth: YearMonth) {
        val calendarDays = generateCalendarDays(yearMonth)
        _uiState.update { it.copy(calendarDays = calendarDays) }
    }

    private fun generateCalendarDays(yearMonth: YearMonth): List<CalendarDay> {
        val firstDayOfMonth = yearMonth.atDay(1)
        
        // 달력 그리드의 시작 지점을 찾습니다 (첫 번째 주를 채우기 위한 이전 달의 날짜들).
        // 그리드는 일요일 시작을 기준으로 합니다.
        val startOfWeek = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        
        // 달력 그리드의 끝 지점을 찾습니다 (마지막 주를 채우기 위한 다음 달의 날짜들).
        // UI 일관성을 위해 항상 6행(42일)을 표시합니다.
        val endOfGrid = startOfWeek.plusDays(41) // 총 42일

        val days = mutableListOf<CalendarDay>()
        var currentDate = startOfWeek
        val today = LocalDate.now()

        while (!currentDate.isAfter(endOfGrid)) {
            days.add(
                CalendarDay(
                    date = currentDate,
                    isCurrentMonth = yearMonth == YearMonth.from(currentDate),
                    isToday = currentDate == today
                )
            )
            currentDate = currentDate.plusDays(1)
        }
        return days
    }
}