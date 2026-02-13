package com.jg.childmomentsnap.feature.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<FeedSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadDiariesForMonth(YearMonth.now())
    }

    fun loadDiariesForMonth(yearMonth: YearMonth) {
        val calculatedMonthlyDays = calculateMonthlyDays(yearMonth)
        
        _uiState.update { it.copy(
            currentMonth = yearMonth,
            monthlyDays = calculatedMonthlyDays
        ) }
        
        // Ensure weekly days are also updated if base date changes or just initial load
        updateWeeklyDays(_uiState.value.selectedDate ?: LocalDate.now())

        viewModelScope.launch {
            diaryRepository.getDiariesByMonth(yearMonth)
                .catch { /* Handle error */ }
                .collect { diaries ->
                    // date format: "yyyy-MM-dd HH:mm:ss"
                    val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val diariesMap = diaries.groupBy { 
                        try {
                            java.time.LocalDateTime.parse(it.date, formatter).toLocalDate()
                        } catch (e: Exception) {
                            // Fallback for old data or errors
                            LocalDate.now() 
                        }
                    }
                    val sortedList = diaries.sortedByDescending { it.date }
                    _uiState.update { it.copy(
                        diaries = diariesMap,
                        feedList = sortedList
                    ) }
                }
        }
    }

    private fun calculateMonthlyDays(currentMonth: YearMonth): List<LocalDate?> {
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfMonth = currentMonth.atDay(1)
        val startOffset = firstDayOfMonth.dayOfWeek.value % 7 // Sun=0, Mon=1...Sat=6 (if Sunday start)
        
        val totalCells = daysInMonth + startOffset
        val rows = (totalCells + 6) / 7
        val totalGridSize = rows * 7
        
        val days = MutableList<LocalDate?>(totalGridSize) { null }
        
        for (day in 1..daysInMonth) {
            days[startOffset + day - 1] = currentMonth.atDay(day)
        }
        return days
    }

    private fun updateWeeklyDays(baseDate: LocalDate) {
        val startOfWeek = baseDate.minusDays(baseDate.dayOfWeek.value.toLong() % 7) // Sunday start
        val days = (0..6).map { startOfWeek.plusDays(it.toLong()) }
        _uiState.update { it.copy(weeklyDays = days) }
    }

    fun onDateClick(date: LocalDate) {
        // Update weekly view to center around selected date or just ensure selected date is visible (standard behavior)
        updateWeeklyDays(date)

        val diaries = uiState.value.diaries[date] ?: emptyList()
        viewModelScope.launch {
            when (diaries.size) {
                0 -> {
                    _uiState.update { it.copy(
                        selectedDate = null,
                        isBottomSheetVisible = false,
                        bottomSheetDiaries = emptyList()
                    ) }
                    _sideEffect.send(FeedSideEffect.ShowWriteSelectionDialog(date))
                }
                1 -> {
                    _uiState.update { it.copy(
                        selectedDate = null,
                        isBottomSheetVisible = false,
                        bottomSheetDiaries = emptyList()
                    ) }
                    _sideEffect.send(FeedSideEffect.NavigateToDetail(diaries.first().id))
                }
                else -> { // N >= 2
                    _uiState.update { it.copy(
                        selectedDate = date,
                        isBottomSheetVisible = true,
                        bottomSheetDiaries = diaries
                    ) }
                }
            }
        }
    }

    fun dismissBottomSheet() {
        _uiState.update { it.copy(
            selectedDate = null,
            isBottomSheetVisible = false,
            bottomSheetDiaries = emptyList()
        ) }
    }

    fun onWriteTypeSelected(isPhoto: Boolean, date: LocalDate) {
        viewModelScope.launch {
            if (isPhoto) {
                _sideEffect.send(FeedSideEffect.NavigateToCamera)
            } else {
                _sideEffect.send(FeedSideEffect.NavigateToWrite(date))
            }
        }
    }

    fun toggleCalendarExpansion() {
        _uiState.update { it.copy(isCalendarExpanded = !it.isCalendarExpanded) }
    }

    fun onFabClick(date: LocalDate) {
        viewModelScope.launch {
            _sideEffect.send(FeedSideEffect.ShowWriteSelectionDialog(date))
        }
    }
}
