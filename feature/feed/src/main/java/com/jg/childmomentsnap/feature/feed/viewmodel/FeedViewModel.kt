package com.jg.childmomentsnap.feature.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.usecase.DeleteDiaryUseCase
import com.jg.childmomentsnap.core.domain.usecase.GetDiariesByDateUseCase
import com.jg.childmomentsnap.core.domain.usecase.ToggleDiaryFavoriteUseCase
import com.jg.childmomentsnap.core.model.Diary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getDiariesByDateUseCase: GetDiariesByDateUseCase,
    private val toggleDiaryFavoriteUseCase: ToggleDiaryFavoriteUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<FeedSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadDiariesForMonthCalendar(YearMonth.now())
        loadDiaryForDay(LocalDate.now())
    }

    fun loadDiariesForMonthCalendar(yearMonth: YearMonth) {
        val calculatedMonthlyDays = calculateMonthlyDays(yearMonth)

        _uiState.update {
            it.copy(
                currentMonth = yearMonth,
                monthlyDays = calculatedMonthlyDays
            )
        }

        updateWeeklyDays(_uiState.value.selectedDate ?: LocalDate.now())
    }

    private fun loadDiaryForDay(date: LocalDate) {
        viewModelScope.launch {
            when (val result = getDiariesByDateUseCase.invoke(date = date)) {
                is DomainResult.Success -> {
                    _uiState.update { current ->
                        current.copy(
                            feedList = result.data
                        )
                    }
                }

                is DomainResult.Fail -> {
                    // Handle error
                }
            }
        }
    }

    private fun calculateMonthlyDays(currentMonth: YearMonth): List<LocalDate?> {
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfMonth = currentMonth.atDay(1)
        val startOffset = firstDayOfMonth.dayOfWeek.value % 7

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
        updateWeeklyDays(date)

        loadDiaryForDay(date)

        _uiState.update {
            it.copy(
                selectedDate = date
            )
        }
    }

    fun toggleCalendarExpansion() {
        _uiState.update { it.copy(isCalendarExpanded = !it.isCalendarExpanded) }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            when (toggleDiaryFavoriteUseCase.invoke(id, !isFavorite)) {
                is DomainResult.Success -> {
                    _uiState.update { current ->
                        val updatedFeedList = current.feedList.map { diary ->
                            if (diary.id == id) {
                                diary.copy(isFavorite = !isFavorite)
                            } else {
                                diary
                            }
                        }
                        current.copy(
                            feedList = updatedFeedList
                        )
                    }
                }

                is DomainResult.Fail -> {
                    // Handle error
                }
            }
        }
    }

    fun onDeleteDiary(diaryId: Long) {
        viewModelScope.launch {
            val diary = _uiState.value.feedList.find { it.id == diaryId } ?: return@launch

            when (deleteDiaryUseCase.invoke(diary)) {
                is DomainResult.Success -> {
                    _uiState.update { current ->
                        val updatedFeedList = current.feedList.filter { it.id != diaryId }
                        current.copy(
                            feedList = updatedFeedList
                        )
                    }
                }
                is DomainResult.Fail -> {
                    // Handle error
                }
            }
        }
    }

}
