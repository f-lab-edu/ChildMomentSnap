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
        _uiState.update { it.copy(currentMonth = yearMonth) }
        viewModelScope.launch {
            diaryRepository.getDiariesByMonth(yearMonth)
                .catch { /* Handle error */ }
                .collect { diaries ->
                    val diariesMap = diaries.groupBy { LocalDate.parse(it.date) }
                    _uiState.update { it.copy(diaries = diariesMap) }
                }
        }
    }

    fun onDateClick(date: LocalDate) {
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
}
