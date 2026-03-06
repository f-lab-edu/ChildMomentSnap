package com.jg.childmomentsnap.feature.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.usecase.DeleteDiaryUseCase
import com.jg.childmomentsnap.core.domain.usecase.GetDiariesByDateUseCase
import com.jg.childmomentsnap.core.domain.usecase.SearchDiaryContentUseCase
import com.jg.childmomentsnap.core.domain.usecase.ToggleDiaryFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
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
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
    private val searchDiaryContentUseCase: SearchDiaryContentUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<FeedSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadDiariesForMonthCalendar(YearMonth.now())
        loadDiaryForDay(LocalDate.now())
        observerSearchQuery()
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

    fun searchDiaryContent(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
        // 빈 검색어일 때 검색 결과 비우기
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    fun toggleSearchMode(isActive: Boolean) {
        _uiState.update { 
            it.copy(
                isSearchMode = isActive,
                searchQuery = if (!isActive) "" else it.searchQuery,
                searchResults = if (!isActive) emptyList() else it.searchResults
            ) 
        }
        if (!isActive) {
            _searchQuery.value = ""
        }
    }

    // --- 달력 네비게이션 ---

    /**
     * 주간 뷰에서 ◀▶ 버튼으로 1주일씩 이동
     * @param direction -1 이면 이전 주, +1 이면 다음 주
     */
    fun navigateWeek(direction: Int) {
        val currentBase = _uiState.value.selectedDate ?: LocalDate.now()
        val newDate = currentBase.plusWeeks(direction.toLong())
        onDateClick(newDate)

        // 월이 바뀌었으면 캘린더도 갱신
        val newMonth = YearMonth.from(newDate)
        if (newMonth != _uiState.value.currentMonth) {
            loadDiariesForMonthCalendar(newMonth)
        }
    }

    /**
     * 월간 뷰에서 ◀▶ 버튼으로 1개월씩 이동
     * @param direction -1 이면 이전 달, +1 이면 다음 달
     */
    fun navigateMonth(direction: Int) {
        val newMonth = _uiState.value.currentMonth.plusMonths(direction.toLong())
        loadDiariesForMonthCalendar(newMonth)
        // 해당 달의 1일을 선택하고 피드 로드
        val firstDay = newMonth.atDay(1)
        onDateClick(firstDay)
    }

    // --- 년/월 선택 다이얼로그 ---

    fun showYearMonthPicker() {
        _uiState.update { it.copy(isYearMonthPickerVisible = true) }
    }

    fun hideYearMonthPicker() {
        _uiState.update { it.copy(isYearMonthPickerVisible = false) }
    }

    fun onYearMonthSelected(yearMonth: YearMonth) {
        hideYearMonthPicker()
        loadDiariesForMonthCalendar(yearMonth)
        val firstDay = yearMonth.atDay(1)
        onDateClick(firstDay)
    }

    private fun observerSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .flatMapLatest { query ->
                    searchDiaryContentUseCase.invoke(query)
                }
                .collect { result ->
                    when(result) {
                        is DomainResult.Success -> {
                            _uiState.update { current ->
                                current.copy(searchResults = result.data)
                            }
                        }
                        is DomainResult.Fail -> {
                            _uiState.update { current ->
                                current.copy(searchResults = emptyList())
                            }
                        }
                    }
                }
        }
    }

}
